package com.mynet.questservice.processors;

import com.mynet.questservice.QuestController;
import com.mynet.shared.network.InvalidServerMessage;
import com.mynet.shared.network.MessageProcessor;
import com.mynet.shared.network.NetworkMessage;

public class RemoveUserProcessor implements MessageProcessor {
    @Override
    public void process(NetworkMessage message) throws InvalidServerMessage {
        QuestController.getInstance().removeUser(message.getId());
    }
}
