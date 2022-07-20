package com.mynet.chatserver.processors;

import com.mynet.chatserver.ChatController;
import com.mynet.shared.network.InvalidServerMessage;
import com.mynet.shared.network.MessageProcessor;
import com.mynet.shared.network.NetworkMessage;

public class RemoveUserProcessor implements MessageProcessor {
    @Override
    public void process(NetworkMessage message) throws InvalidServerMessage {
        ChatController.getInstance().removeUser(message.getId());
    }
}
