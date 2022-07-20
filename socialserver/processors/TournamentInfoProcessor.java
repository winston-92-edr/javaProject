package com.mynet.socialserver.processors;

import com.mynet.shared.model.TournamentLevelController;
import com.mynet.shared.network.GameCommands;
import com.mynet.shared.network.InvalidServerMessage;
import com.mynet.shared.network.MessageProcessor;
import com.mynet.shared.network.NetworkMessage;
import com.mynet.shared.response.UserLoginResponse;
import com.mynet.shared.types.ServerType;
import com.mynet.shared.user.ProxyUser;
import com.mynet.socialserver.SocialController;

public class TournamentInfoProcessor implements MessageProcessor {
    @Override
    public void process(NetworkMessage message) throws InvalidServerMessage {
        SocialController socialController = SocialController.getInstance();
        ProxyUser user = socialController.getUser(message.getId());

        UserLoginResponse.Builder builder = new UserLoginResponse.Builder();
        builder.setServerType(ServerType.TOURNAMENT);
        builder.setTournamentList(TournamentLevelController.getInstance().getTournamentForUserInit(user));
        builder.setTickets(user.getTickets());

        UserLoginResponse response = builder.build();
        NetworkMessage networkMessage = new NetworkMessage(GameCommands.GET_TOURNAMENTS_INFO);
        networkMessage.setDataAsJSON(response);
        networkMessage.setSuccess(true);
        socialController.getNodeToProxy().addServerMessage(networkMessage, user);

    }
}
