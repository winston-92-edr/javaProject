package com.mynet.socialserver.processors;

import com.mynet.proxyserver.user.UserModel;
import com.mynet.shared.network.GameCommands;
import com.mynet.shared.network.InvalidServerMessage;
import com.mynet.shared.network.MessageProcessor;
import com.mynet.shared.network.NetworkMessage;
import com.mynet.shared.request.GoToFriendRequest;
import com.mynet.shared.resource.CacheController;
import com.mynet.shared.response.GoToFriendResponse;
import com.mynet.shared.user.ProxyUser;
import com.mynet.socialserver.SocialController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GoToFriendProcessor implements MessageProcessor {
    Logger logger = LoggerFactory.getLogger(GoToFriendProcessor.class);
    @Override
    public void process(NetworkMessage message) throws InvalidServerMessage {
        try {
            ProxyUser user = SocialController.getInstance().getUser(message.getId());
            String friendId = message.getData();
            ProxyUser friend = SocialController.getInstance().getUser(friendId);

            if (friend != null) {
                CacheController cacheController = CacheController.getInstance();
                UserModel um = cacheController.getUserGameModel(friendId);
                NetworkMessage response = new NetworkMessage(GameCommands.GO_TO_FRIEND);
                response.setData(um.roomID  + "/" + um.tableID  + "/" + um.gameID);
                SocialController.getInstance().getNodeToProxy().addServerMessage(response, user);
            } else {
                NetworkMessage response = new NetworkMessage(GameCommands.GO_TO_FRIEND);
                response.setData("-1/-1/-1");
                SocialController.getInstance().getNodeToProxy().addServerMessage(response, user);
            }
        }catch (Exception e){
            logger.error(e.getMessage());
        }
    }
}
