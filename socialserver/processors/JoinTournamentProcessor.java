package com.mynet.socialserver.processors;

import com.mynet.bonusservice.model.type.BonusRuleTypes;
import com.mynet.gameserver.enums.ErrorCode;
import com.mynet.shared.config.ServerGlobalVariables;
import com.mynet.shared.logs.BonusLog;
import com.mynet.shared.logs.RabbitMQLogController;
import com.mynet.shared.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.mynet.shared.logs.TournamentEventLog;
import com.mynet.shared.network.GameCommands;
import com.mynet.shared.network.InvalidServerMessage;
import com.mynet.shared.network.MessageProcessor;
import com.mynet.shared.network.NetworkMessage;
import com.mynet.shared.request.JoinTournamentRequest;
import com.mynet.shared.resource.CacheController;
import com.mynet.shared.resource.db.DBController;
import com.mynet.socialserver.model.TicketErrorModel;
import com.mynet.shared.response.UpdateTicketResponse;
import com.mynet.shared.types.GamePlayStatusType;
import com.mynet.shared.user.ProxyUser;
import com.mynet.socialserver.SocialController;

public class JoinTournamentProcessor implements MessageProcessor {
    private static final Logger logger = LoggerFactory.getLogger(JoinTournamentProcessor.class);

    @Override
        public void process(NetworkMessage message) throws InvalidServerMessage {
        JoinTournamentRequest request = NetworkMessage.CreateMessage(message.getData(), JoinTournamentRequest.class);

        NetworkMessage response = new NetworkMessage(GameCommands.JOIN_TOURNAMENT);

        SocialController socialController = SocialController.getInstance();
        ProxyUser user = socialController.getUser(message.getId());

        boolean maintenanceMode = ServerGlobalVariables.getInstance().getString("TOURNAMENT_MAINTENANCE_MODE", "false").equals("true");
        if(maintenanceMode){
            socialController.getNodeToProxy().sendError(user, ErrorCode.MAINTENANCE_MODE);
            return;
        }

        TournamentModel tournament = TournamentLevelController.getInstance().getTournament(request.getTournamentId());
        if (tournament == null || tournament.getLevels().size() == 0) {
            socialController.getNodeToProxy().sendError(user, ErrorCode.TOURNAMENT_NOT_AVAILABLE);
            return;
        }

        if(!tournament.isActive()){
            socialController.getNodeToProxy().sendError(user, ErrorCode.TOURNAMENT_NOT_ACTIVE);
            return;
        }

        int remainCount = tournament.getMaxTryCount();
        int cost = tournament.getCost();

        TournamentEventLog.Type tournamentEventType = TournamentEventLog.Type.JOIN;

        UserTournamentModel tm = user.getTournament(tournament.getTournamentId());
        if (tm != null) {
            if (tm.isOver()) {
                if (tm.claimed) {
                    // this shouldn't be there
                    logger.error("JoinTournamentProcessor claimed: " + NetworkMessage.getGson().toJson(tm));
                    return;
                }

                if (tm.isSucceed() && !tm.claimed) {
                    // needs claim first
                    return;
                }

                if (tm.isFailed() && tm.remainingTryCount > 0) {
                    remainCount = tm.remainingTryCount;
                    remainCount -= 1;
                    tm.lostGames -= 1;
                    tm.state = UserTournamentState.ACTIVE;
                    cost = tournament.getLevel(tm.level).getRedeem();

                    tournamentEventType = TournamentEventLog.Type.CONTINUE;

                } else {
                    // something wrong
                    logger.error("JoinTournamentProcessor isFailed: " + NetworkMessage.getGson().toJson(tm));
                    CacheController.getInstance().removeUserTournamentModel(user.getId(), tournament.getTournamentId());
                    socialController.getNodeToProxy().sendError(user, ErrorCode.GENERAL_ERROR);
                    return;
                }
            }else{
                // already in the tournament
                socialController.getNodeToProxy().sendError(user, ErrorCode.USER_ALREADY_IN_TOURNAMENT);
                return;
            }
        }else {
            TournamentLevel entranceLevel = tournament.getLevel(TournamentLevel.ENTRANCE_LEVEL_ID);
            tm = UserTournamentModel.create(TournamentLevel.ENTRANCE_LEVEL_ID, entranceLevel, tournament.getFailPoint(), tournament.getMaxLevelId(), tournament.getMaxTryCount(), tournament.getTournamentId(),tournament.getEndDate());
        }

        GamePlayStatusType error = charge(user, tournament.getTournamentId(), cost, tm.level, socialController);
        if (error != GamePlayStatusType.VALID) {
            if (error == GamePlayStatusType.NOT_ENOUGH_TICKET) {
                socialController.getNodeToProxy().sendCustomError(user, ErrorCode.NOT_ENOUGH_TICKET, NetworkMessage.getGson().toJson(new TicketErrorModel(tournament.getTitle(), cost)));
            } else {
                socialController.getNodeToProxy().sendError(user, ErrorCode.GENERAL_ERROR);

            }

            //refresh state
            user.getTournament(tournament.getTournamentId());
            return;
        }

        tm.remainingTryCount = remainCount;

        if(tournament.getLevels().size() < tm.level){
            tm.level = tournament.getLevels().size();
        }

        user.setTournament(tournament.getTournamentId(), tm);

        user.beginTournamentEvent(tournamentEventType);
        user.fillTournamentEvent(tournament.getTournamentId(), true, -1, cost, 0);
        user.endTournamentEvent();

        user.incrementTotalTournament(tournament.getTournamentId());

        response.setCmd(GameCommands.CHECK_USER_TOURNAMENT);
        response.setDataAsJSON(tm);
        response.setSuccess(true);
        socialController.getNodeToProxy().addServerMessage(response, user);
    }

    private GamePlayStatusType charge(ProxyUser user, int tournamentId, int cost, int levelId, SocialController socialController) {
        if (cost > 0) {
            // needs to user to be charged in order to join
            if (!user.canAffordTickets(cost)) {
                return GamePlayStatusType.NOT_ENOUGH_TICKET;
            }

            boolean success = DBController.getInstance().updateUserTickets(user.getId(), cost, false);
            if(!success){
                return GamePlayStatusType.GENERAL_ERROR;
            }

            user.decreaseTickets(cost);

            user.writeUserTicketLog("charge_tournament_" + tournamentId, levelId, -cost, System.currentTimeMillis());


            NetworkMessage message = new NetworkMessage(GameCommands.USER_TICKET_UPDATED);
            UpdateTicketResponse response = new UpdateTicketResponse(user.getTickets(), UpdateTicketResponse.UpdateTicketReason.DEFAULT);
            message.setDataAsJSON(response);
            socialController.getNodeToProxy().addServerMessage(message, user);

            RabbitMQLogController.getInstance().addBonusLog(new BonusLog(BonusRuleTypes.TICKET_AMOUNT, user.getId(), user.getUserModel().gamesTotal, user.getMoney(), user.getUserModel().tickets, user.getUserModel().joinDate));
        }
        return GamePlayStatusType.VALID;
    }
}
