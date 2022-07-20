package com.mynet.gameserver.processors;

import com.mynet.gameserver.GameController;
import com.mynet.gameserver.okey.Table;
import com.mynet.matchserver.GameUser;
import com.mynet.shared.network.GameCommands;
import com.mynet.shared.network.InvalidServerMessage;
import com.mynet.shared.network.MessageProcessor;
import com.mynet.shared.network.NetworkMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientSuspendProcessor implements MessageProcessor {
    Logger logger = LoggerFactory.getLogger(ClientSuspendProcessor.class);

    @Override
    public void process(NetworkMessage message) throws InvalidServerMessage {
        try {

            GameController controller = GameController.getInstance();
            GameUser user = controller.getUser(message.getId());

            if (user == null) return;

            user.setSuspended(true);

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }
}
