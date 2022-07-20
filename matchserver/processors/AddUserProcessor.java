package com.mynet.matchserver.processors;

import com.mynet.matchserver.MatchMakingController;
import com.mynet.shared.network.InvalidServerMessage;
import com.mynet.shared.network.MessageProcessor;
import com.mynet.shared.network.NetworkMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.naming.CannotProceedException;

public class AddUserProcessor implements MessageProcessor {
    private static Logger logger = LoggerFactory.getLogger(AddUserProcessor.class);
    @Override
    public void process(NetworkMessage message) throws InvalidServerMessage {
        try {
            MatchMakingController.getInstance().createUser(message.getId());
        } catch (CannotProceedException e) {
            logger.error(e.getMessage(), e);
        }

    }
}
