package com.mynet.gameserver.processors;

import com.mynet.gameserver.GameController;
import com.mynet.shared.network.InvalidServerMessage;
import com.mynet.shared.network.MessageProcessor;
import com.mynet.shared.network.NetworkMessage;

public class RemoveUserProcessor implements MessageProcessor {
    @Override
    public void process(NetworkMessage message) throws InvalidServerMessage {
        GameController.getInstance().removeUser(message.getId(), true);
    }
}
