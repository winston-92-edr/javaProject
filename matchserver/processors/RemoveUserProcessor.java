package com.mynet.matchserver.processors;

import com.mynet.matchserver.MatchMakingController;
import com.mynet.shared.network.InvalidServerMessage;
import com.mynet.shared.network.MessageProcessor;
import com.mynet.shared.network.NetworkMessage;

public class RemoveUserProcessor implements MessageProcessor {
    @Override
    public void process(NetworkMessage message) throws InvalidServerMessage {
        MatchMakingController.getInstance().removeUser(message.getId());
    }
}
