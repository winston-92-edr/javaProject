package com.mynet.socialserver.processors;

import com.mynet.proxyserver.user.UserModel;
import com.mynet.shared.network.GameCommands;
import com.mynet.shared.network.InvalidServerMessage;
import com.mynet.shared.network.MessageProcessor;
import com.mynet.shared.network.NetworkMessage;
import com.mynet.shared.request.GoToFriendRequest;
import com.mynet.shared.resource.CacheController;
import com.mynet.shared.response.GoToYourFriendResponse;
import com.mynet.shared.user.ProxyUser;
import com.mynet.socialserver.SocialController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GoToYourFriendProcessor implements MessageProcessor {
    Logger logger = LoggerFactory.getLogger(GoToYourFriendProcessor.class);
    @Override
    public void process(NetworkMessage message) throws InvalidServerMessage {
        try {
            GoToFriendRequest request = NetworkMessage.CreateMessage(message.getData(), GoToFriendRequest.class);
            ProxyUser user = SocialController.getInstance().getUser(message.getId());
            ProxyUser friend = SocialController.getInstance().getUser(request.getFuid());

            if (friend != null) {
                CacheController cacheController = CacheController.getInstance();
                UserModel um = cacheController.getUserGameModel(request.getFuid());
                NetworkMessage response = new NetworkMessage(GameCommands.GO_TO_YOUR_FRIEND);
                response.setDataAsJSON(new GoToYourFriendResponse(um.gameID, um.roomID));
                SocialController.getInstance().getNodeToProxy().addServerMessage(response, user);
            }
        }catch (Exception e){
            logger.error(e.getMessage());
        }
    }
}
