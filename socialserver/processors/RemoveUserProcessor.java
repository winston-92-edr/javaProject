package com.mynet.socialserver.processors;

import com.mynet.shared.network.InvalidServerMessage;
import com.mynet.shared.network.MessageProcessor;
import com.mynet.shared.network.NetworkMessage;
import com.mynet.socialserver.SocialController;

public class RemoveUserProcessor implements MessageProcessor {
    @Override
    public void process(NetworkMessage message) throws InvalidServerMessage {
        SocialController.getInstance().removeUser(message.getId());
    }
}
