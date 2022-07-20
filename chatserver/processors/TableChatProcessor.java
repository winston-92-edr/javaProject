package com.mynet.chatserver.processors;

import com.mynet.chatserver.ChatController;
import com.mynet.chatserver.models.ChatUser;
import com.mynet.gameserver.request.TableChatRequest;
import com.mynet.shared.network.InvalidServerMessage;
import com.mynet.shared.network.MessageProcessor;
import com.mynet.shared.network.NetworkMessage;

public class TableChatProcessor implements MessageProcessor {
    @Override
    public void process(NetworkMessage message) throws InvalidServerMessage {
        try {
            ChatController controller = ChatController.getInstance();
            ChatUser user = controller.getUser(message.getId());
            if(user != null) {
                TableChatRequest request = NetworkMessage.CreateMessage(message.getData(), TableChatRequest.class);

                if(request.getMessage().length() > 100) return;

                ChatController.getInstance().sendTableChatMessage(user.getId(), request.getMessage());
            }
        }catch (Exception e){

        }
    }
}
