package com.mynet.socialserver.processors;

import com.mynet.shared.network.GameCommands;
import com.mynet.shared.network.InvalidServerMessage;
import com.mynet.shared.network.MessageProcessor;
import com.mynet.shared.network.NetworkMessage;
import com.mynet.shared.request.RemoveFriendRequest;
import com.mynet.shared.resource.CacheController;
import com.mynet.shared.resource.db.DBController;
import com.mynet.shared.user.ProxyUser;
import com.mynet.socialserver.SocialController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.security.util.Cache;

public class RemoveFriendProcessor implements MessageProcessor {
    Logger logger = LoggerFactory.getLogger(RemoveFriendProcessor.class);
    @Override
    public void process(NetworkMessage message) throws InvalidServerMessage {
        try{
            RemoveFriendRequest request = NetworkMessage.CreateMessage(message.getData(),RemoveFriendRequest.class);

            String deletedId = request.getId();

            DBController.getInstance().removeFriend(message.getId(), deletedId);

            SocialController socialController = SocialController.getInstance();

            ProxyUser user = socialController.getUser(message.getId());

            user.removeOnlineFriend(deletedId);

            NetworkMessage updateFriendsMessage = new NetworkMessage(GameCommands.UPDATE_FRIENDS);

            socialController.getNodeToProxy().addServerMessage(updateFriendsMessage, user);

            ProxyUser deleted = socialController.getUser(deletedId);

            if(deleted != null){
                deleted.removeOnlineFriend(user.getId());

                socialController.getNodeToProxy().addServerMessage(updateFriendsMessage, deleted);
            }

            //CacheController.getInstance().removeOnlineFriend(user.getId(), request.getId());
        }
        catch (Exception e){
            logger.error(e.getMessage());
        }
    }
}
