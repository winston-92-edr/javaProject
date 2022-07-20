package com.mynet.chatserver.processors;

import com.mynet.chatserver.ChatController;
import com.mynet.chatserver.models.ChatUser;
import com.mynet.chatserver.request.SetProfanityFilterRequest;
import com.mynet.shared.network.InvalidServerMessage;
import com.mynet.shared.network.MessageProcessor;
import com.mynet.shared.network.NetworkMessage;

public class SetProfanityFilterProcessor implements MessageProcessor {
    @Override
    public void process(NetworkMessage message) throws InvalidServerMessage {
        ChatUser chatUser = ChatController.getInstance().getUser(message.getId());

        if(chatUser != null){
            SetProfanityFilterRequest request = NetworkMessage.CreateMessage(message.getData(), SetProfanityFilterRequest.class);
            chatUser.setProfanityFilter(request.isFilter());
        }
    }
}
