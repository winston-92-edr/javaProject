package com.mynet.gameserver.processors;

import com.mynet.gameserver.GameController;
import com.mynet.gameserver.request.EnterRoomRequest;
import com.mynet.gameserver.room.Room;
import com.mynet.matchserver.GameUser;
import com.mynet.shared.network.GameCommands;
import com.mynet.shared.network.InvalidServerMessage;
import com.mynet.shared.network.MessageProcessor;
import com.mynet.shared.network.NetworkMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.StringTokenizer;

public class EnterRoomProcessor implements MessageProcessor {
    private static Logger logger = LoggerFactory.getLogger(EnterRoomProcessor.class);

    @Override
    public void process(NetworkMessage message) throws InvalidServerMessage {
        GameController controller = GameController.getInstance();
        GameUser user = controller.getUser(message.getId());
        try {
            EnterRoomRequest request = NetworkMessage.getGson().fromJson(message.getData(), EnterRoomRequest.class);
            int roomId = request.getRoomId();
            controller.enterRoom(roomId, user);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

    }
}
