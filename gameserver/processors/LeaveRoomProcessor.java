package com.mynet.gameserver.processors;

import com.mynet.gameserver.GameController;
import com.mynet.gameserver.room.Room;
import com.mynet.matchserver.GameUser;
import com.mynet.shared.network.InvalidServerMessage;
import com.mynet.shared.network.MessageProcessor;
import com.mynet.shared.network.NetworkMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LeaveRoomProcessor implements MessageProcessor {
    Logger logger = LoggerFactory.getLogger(LeaveRoomProcessor.class);

    @Override
    public void process(NetworkMessage message) throws InvalidServerMessage {
        try {
            GameController controller = GameController.getInstance();
            GameUser user = controller.getUser(message.getId());

            int roomId = user.getRoomId();
            Room room = controller.getRoom(roomId);

            if (room != null) {
                room.RemoveUser(user);
            }
            
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }
}
