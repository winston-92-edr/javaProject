package com.mynet.gameserver.processors;

import com.mynet.gameserver.GameController;
import com.mynet.matchserver.GameUser;
import com.mynet.shared.network.InvalidServerMessage;
import com.mynet.shared.network.MessageProcessor;
import com.mynet.shared.network.NetworkMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.StringTokenizer;

public class JoinAudienceProcessor implements MessageProcessor {
    private static Logger logger = LoggerFactory.getLogger(JoinAudienceProcessor.class);

    @Override
    public void process(NetworkMessage message) throws InvalidServerMessage {
//        GameController controller = GameController.getInstance();
//        GameUser user = controller.getUser(message.getId());
//
//        try {
//            String[] strings = message.getData().split(";");
//            String tableId = strings[0];
//            controller.joinAnAudience(user, tableId);
//        } catch (Exception e) {
//            logger.error(e.getMessage(), e);
//        }

    }
}
