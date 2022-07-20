package com.mynet.gameserver.processors;

import com.mynet.gameserver.GameController;
import com.mynet.matchserver.GameUser;
import com.mynet.shared.network.GameCommands;
import com.mynet.shared.network.InvalidServerMessage;
import com.mynet.shared.network.MessageProcessor;
import com.mynet.shared.network.NetworkMessage;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GoBehindFriendProcessor implements MessageProcessor {
    private static Logger logger = LoggerFactory.getLogger(GoBehindFriendProcessor.class);

    @Override
    public void process(NetworkMessage message) throws InvalidServerMessage {
        try {
            GameController controller = GameController.getInstance();

            GameUser user = controller.getUser(message.getId());
            GameUser friendUser = controller.getUser(message.getData());

            JSONObject json = new JSONObject();
            json.put("result", false);
            int errorCode = -1;

            if (friendUser != null) {
                if (friendUser.getRoomId() > 0) {
                    if (friendUser.getRoomId() != user.getRoomId()) {
                        controller.enterRoom(friendUser.getRoomId(), friendUser);
                        if (friendUser.getTableId() >= 0) {
                            if (user.getRoomId() != 0) {
                                JoinAudienceProcessor processor = new JoinAudienceProcessor();
                                processor.process(new NetworkMessage(user.getId(), GameCommands.JOIN_AN_AUDIENCE, friendUser.getTableId() + ""));
                                json.put("result", true);
                            } else {
                                errorCode = 5; // Enter room fail!
                            }
                        } else {
                            errorCode = 4; // Friend has not proper table!
                        }
                    } else {
                        errorCode = 3; // User already in same room with friend.
                    }
                } else {
                    errorCode = 2; // Friend is not in proper room.
                }
            } else {
                errorCode = 1; // User not in this server!
            }
            json.put("error_code", errorCode);
            json.put("fuid", friendUser.getId());
            json.put("c", GameCommands.GO_BEHIND_FRIEND);
            json.put("type", 2);

            controller.sendNetworkMessage(user, GameCommands.GO_TO_FRIEND, json.toJSONString());

        }catch (Exception e){
            logger.error(e.getMessage(), e);
        }
    }
}
