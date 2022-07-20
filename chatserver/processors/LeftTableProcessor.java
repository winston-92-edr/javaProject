package com.mynet.chatserver.processors;

import com.mynet.chatserver.ChatController;
import com.mynet.chatserver.models.ChatUser;
import com.mynet.shared.network.InvalidServerMessage;
import com.mynet.shared.network.MessageProcessor;
import com.mynet.shared.network.NetworkMessage;
import com.mynet.shared.resource.CacheController;

public class LeftTableProcessor implements MessageProcessor {
    @Override
    public void process(NetworkMessage message) throws InvalidServerMessage {
        try {
            ChatController controller = ChatController.getInstance();
            ChatUser user = controller.getUser(message.getId());
            if(user != null) {
                controller.removeUserGroups(user.getId());
            }
        }catch (Exception e){

        }
    }
}
