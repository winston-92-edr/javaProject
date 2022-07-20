package com.mynet.matchserver.processors;

import com.mynet.matchserver.MatchMakingController;
import com.mynet.matchserver.MatchRequest;
import com.mynet.matchserver.GameUser;
import com.mynet.matchserver.model.GameTypeInfo;
import com.mynet.shared.model.TournamentLevelController;
import com.mynet.shared.model.UserTournamentModel;
import com.mynet.shared.network.GameCommands;
import com.mynet.shared.network.InvalidServerMessage;
import com.mynet.shared.network.MessageProcessor;
import com.mynet.shared.network.NetworkMessage;
import com.mynet.shared.request.TournamentMatchingRequest;

public class TournamentCancelMatchingProcessor implements MessageProcessor {
    @Override
    public void process(NetworkMessage message) throws InvalidServerMessage {
        TournamentMatchingRequest tournamentRequest = NetworkMessage.CreateMessage(message.getData(), TournamentMatchingRequest.class);
        MatchMakingController controller = MatchMakingController.getInstance();
        GameUser user = controller.getUser(message.getId());
        if(user == null) return;
        MatchMakingController.getInstance().removeUser(user.getId());

        TournamentLevelController tournamentController = TournamentLevelController.getInstance();
        GameTypeInfo typeInfo = tournamentController.getGameTypeInfo(tournamentRequest.getTournamentId());
        MatchRequest request = new MatchRequest(user, typeInfo, tournamentRequest.getTournamentId());
        boolean removed = controller.cancelMatchRequest(request);
        if(removed){
            UserTournamentModel utm = user.getTournament(tournamentRequest.getTournamentId());
            NetworkMessage response = new NetworkMessage(GameCommands.CANCEL_MATCHMAKING);
            response.setDataAsJSON(utm);
            response.setSuccess(true);

            controller.getNodeToProxy().addServerMessage(response, user);
        }


    }
}
