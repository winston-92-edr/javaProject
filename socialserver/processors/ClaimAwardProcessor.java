package com.mynet.socialserver.processors;

import com.mynet.gameserver.enums.ErrorCode;
import com.mynet.shared.model.TournamentLevelController;
import com.mynet.shared.model.TournamentModel;
import com.mynet.shared.model.UserTournamentModel;
import com.mynet.shared.network.GameCommands;
import com.mynet.shared.network.InvalidServerMessage;
import com.mynet.shared.network.MessageProcessor;
import com.mynet.shared.network.NetworkMessage;
import com.mynet.shared.request.ClaimAwardRequest;
import com.mynet.shared.types.ClaimAwardType;
import com.mynet.shared.types.GamePlayStatusType;
import com.mynet.shared.user.ProxyUser;
import com.mynet.socialserver.SocialController;

public class ClaimAwardProcessor implements MessageProcessor {
    @Override
    public void process(NetworkMessage message) throws InvalidServerMessage {

        SocialController socialController = SocialController.getInstance();
        ProxyUser user = socialController.getUser(message.getId());

        ClaimAwardRequest request = NetworkMessage.CreateMessage(message.getData(), ClaimAwardRequest.class);
        int id = request.getAwardId();

        if (request.getType() == ClaimAwardType.TOURNAMENT) {
            UserTournamentModel utm = user.getTournament(id);
            if (utm != null) {
                TournamentModel tournament = TournamentLevelController.getInstance().getTournament(id);
                if (tournament == null) {
                    socialController.getNodeToProxy().sendError(user, ErrorCode.TOURNAMENT_NOT_AVAILABLE);
                    return;
                }

                if (utm.isOver()) {
                    // sending CLAIM_AWARD message to the client
                    if (!user.claimTournamentAward(tournament)) {
                        socialController.getNodeToProxy().sendError(user, ErrorCode.GENERAL_ERROR);
                        return;
                    }

                }

            }
        }
    }
}
