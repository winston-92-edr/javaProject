package com.mynet.gameserver.processors;

import com.mynet.gameserver.GameController;
import com.mynet.matchserver.GameUser;
import com.mynet.shared.network.InvalidServerMessage;
import com.mynet.shared.network.MessageProcessor;
import com.mynet.shared.network.NetworkMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.StringTokenizer;

public class EnterRoom2Processor implements MessageProcessor {
    private static Logger logger = LoggerFactory.getLogger(EnterRoom2Processor.class);

    @Override
    public void process(NetworkMessage message) throws InvalidServerMessage {
        GameController controller = GameController.getInstance();
        GameUser user = controller.getUser(message.getId());
        try {
            StringTokenizer st = new StringTokenizer(message.getData(), "#");
            StringTokenizer mainToken = new StringTokenizer(st.nextToken(), ";");
            String strRoomId = mainToken.nextToken();
            controller.enterRoom(Integer.parseInt(strRoomId), user);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

    }
}
