package com.mynet.matchserver.processors;

import com.mynet.gameserver.enums.ErrorCode;
import com.mynet.matchserver.GameUser;
import com.mynet.matchserver.MatchMakingController;
import com.mynet.matchserver.MatchRequest;
import com.mynet.shared.connection.NodeToProxy;
import com.mynet.matchserver.model.GameTypeInfo;
import com.mynet.matchserver.response.MatchingResponse;
import com.mynet.shared.logs.TournamentEventLog;
import com.mynet.shared.model.TournamentLevelController;
import com.mynet.shared.model.TournamentModel;
import com.mynet.shared.model.UserTournamentModel;
import com.mynet.shared.network.GameCommands;
import com.mynet.shared.network.InvalidServerMessage;
import com.mynet.shared.network.MessageProcessor;
import com.mynet.shared.network.NetworkMessage;
import com.mynet.shared.request.TournamentMatchingRequest;

public class TournamentMatchingProcessor implements MessageProcessor {
    @Override
    public void process(NetworkMessage message) throws InvalidServerMessage {
        TournamentMatchingRequest tournamentRequest = NetworkMessage.CreateMessage(message.getData(), TournamentMatchingRequest.class);
        NetworkMessage response = new NetworkMessage(GameCommands.TOURNAMENT_MATCHMAKING);

        MatchMakingController controller = MatchMakingController.getInstance();
        GameUser user = controller.getUser(message.getId());
        if(user == null) return;
        NodeToProxy mp = controller.getNodeToProxy();


        if(controller.isInMaintenance()){
            mp.sendError(user, ErrorCode.MAINTENANCE_MODE);
            return;
        }

        user.refreshNodeInfo();
        if(user.hasTable()){
            mp.sendError(user, ErrorCode.ALREADY_HAVE_TABLE);
            return;
        }

        TournamentModel tournament = TournamentLevelController.getInstance().getTournament(tournamentRequest.getTournamentId());
        if (tournament == null || tournament.getLevels().isEmpty()) {
            mp.sendError(user, ErrorCode.TOURNAMENT_NOT_AVAILABLE);
            return;
        }

        if(!tournament.isActive()){
            mp.sendError(user, ErrorCode.TOURNAMENT_NOT_ACTIVE);
            return;
        }

        UserTournamentModel tm = user.getTournament(tournament.getTournamentId());


        if (tm == null) {
            // tournament has never been assigned to the user or start over again
            return;
        }

        if (tm.isOver()) {
            mp.sendError(user, ErrorCode.GENERAL_ERROR);
            return;
        }

        TournamentLevelController tournamentController = TournamentLevelController.getInstance();
        GameTypeInfo typeInfo = tournamentController.getGameTypeInfo(tournamentRequest.getTournamentId());
        if(typeInfo != null){
            MatchRequest request = new MatchRequest(user, typeInfo, tournamentRequest.getTournamentId());
            user.beginTournamentEvent(TournamentEventLog.Type.MATCH);
            boolean added = controller.addMatchRequest(request);
            if(added){
                MatchingResponse matchingResponse = new MatchingResponse.Builder()
                        .sideCount(tournamentController.getTournament(tournamentRequest.getTournamentId()).getSideCount())
                        .buildInitial();
                response.setSuccess(true);
                response.setDataAsJSON(matchingResponse);
                mp.addServerMessage(response, user);
            }else{
                mp.sendError(user, ErrorCode.GENERAL_ERROR);
            }
        }

    }
}
