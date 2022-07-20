package com.mynet.socialserver.processors;

import com.mynet.gameserver.model.RemoveGameUserModel;
import com.mynet.socialserver.request.AddUserSocialRequest;
import com.mynet.shared.network.InvalidServerMessage;
import com.mynet.shared.network.MessageProcessor;
import com.mynet.shared.network.NetworkMessage;
import com.mynet.shared.resource.CacheController;
import com.mynet.socialserver.SocialController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.naming.CannotProceedException;

public class AddUserProcessor implements MessageProcessor {
    private static Logger logger = LoggerFactory.getLogger(AddUserProcessor.class);

    @Override
    public void process(NetworkMessage message) throws InvalidServerMessage {
        try {
            AddUserSocialRequest request = NetworkMessage.getGson().fromJson(message.getData(), AddUserSocialRequest.class);
            String userId = message.getId();
            int friendsCount = request.getFriendsCount();
            SocialController.getInstance().createUser(userId, friendsCount);
            CacheController.getInstance().publishRemoveSocialUser(new RemoveGameUserModel(SocialController.getInstance().getNodeId(), message.getId()));

        }catch (CannotProceedException e){
            logger.error(e.getMessage(), e);
        }
    }
}
