package com.mynet.chatserver.processors;

import com.mynet.chatserver.ChatController;
import com.mynet.chatserver.models.ChatUser;
import com.mynet.chatserver.request.ChatEnterTableRequest;
import com.mynet.shared.network.InvalidServerMessage;
import com.mynet.shared.network.MessageProcessor;
import com.mynet.shared.network.NetworkMessage;

public class EnterTableProcessor implements MessageProcessor {
    @Override
    public void process(NetworkMessage message) throws InvalidServerMessage {
        try {
            ChatController controller = ChatController.getInstance();
            ChatUser user = controller.getUser(message.getId());
            if(user != null) {
                ChatEnterTableRequest request = NetworkMessage.CreateMessage(message.getData(), ChatEnterTableRequest.class);
                ChatController.getInstance().addUserGroup(user, request);
            }
        }catch (Exception e){

        }
    }
}
