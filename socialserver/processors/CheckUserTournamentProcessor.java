package com.mynet.socialserver.processors;

import com.mynet.gameserver.enums.ErrorCode;
import com.mynet.shared.model.TournamentLevelController;
import com.mynet.shared.model.TournamentModel;
import com.mynet.shared.model.UserTournamentModel;
import com.mynet.shared.network.GameCommands;
import com.mynet.shared.network.InvalidServerMessage;
import com.mynet.shared.network.MessageProcessor;
import com.mynet.shared.network.NetworkMessage;
import com.mynet.shared.request.CheckUserTournamentsRequest;
import com.mynet.shared.response.ClaimAwardResponse;
import com.mynet.shared.types.ClaimAwardType;
import com.mynet.shared.types.GamePlayStatusType;
import com.mynet.shared.user.ProxyUser;
import com.mynet.socialserver.SocialController;

public class CheckUserTournamentProcessor implements MessageProcessor {
    @Override
    public void process(NetworkMessage message) throws InvalidServerMessage {
        CheckUserTournamentsRequest userRequest = NetworkMessage.CreateMessage(message.getData(), CheckUserTournamentsRequest.class);

        SocialController socialController = SocialController.getInstance();
        ProxyUser user = socialController.getUser(message.getId());

        NetworkMessage response = new NetworkMessage(GameCommands.CHECK_USER_TOURNAMENT);

        UserTournamentModel utm = user.getTournament(userRequest.id);
        if (utm != null) {
            TournamentModel tournament = TournamentLevelController.getInstance().getTournament(userRequest.id);
            if (tournament == null) {
                socialController.getNodeToProxy().sendError(user, ErrorCode.TOURNAMENT_NOT_AVAILABLE);
                return;
            }

            response.setDataAsJSON(utm);
            response.setSuccess(true);
            socialController.getNodeToProxy().addServerMessage(response, user);

            if (utm.isOver() && !utm.claimed) { // keep going, checking the tournament reach the target
                // send claim message
                NetworkMessage responseClaim = new NetworkMessage(GameCommands.CLAIM_AWARD);
                ClaimAwardResponse claimMessage = new ClaimAwardResponse.Builder(ClaimAwardType.TOURNAMENT)
                        .setAmount(utm.award)
                        .setConsumed(false)
                        .setAwardId(tournament.getTournamentId())
                        .setDesc(utm.desc).buid();
                responseClaim.setDataAsJSON(claimMessage);
                socialController.getNodeToProxy().addServerMessage(responseClaim, user);
            }

        } else {
            //lobby.sendError(user, response, GameError.USER_TOURNAMENT_NOT_AVAILABLE);
            //response.setDataAsJSON(null);
            response.setSuccess(true);
            socialController.getNodeToProxy().addServerMessage(response, user);
        }
    }
}
