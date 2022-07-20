package com.mynet.gameserver.processors;

import com.mynet.gameserver.GameController;
import com.mynet.gameserver.actions.Quick_Start_Game;
import com.mynet.gameserver.enums.GameStatus;
import com.mynet.gameserver.okey.Table;
import com.mynet.matchserver.GameUser;
import com.mynet.shared.network.GameCommands;
import com.mynet.shared.network.InvalidServerMessage;
import com.mynet.shared.network.MessageProcessor;
import com.mynet.shared.network.NetworkMessage;
import com.mynet.shared.resource.CacheController;
import com.mynet.shared.response.UserStateResponse;
import com.mynet.shared.types.ServerType;
import com.mynet.shared.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientAwakeProcessor implements MessageProcessor {
    Logger logger = LoggerFactory.getLogger(ClientAwakeProcessor.class);

    @Override
    public void process(NetworkMessage message) throws InvalidServerMessage {
        try {

            GameController controller = GameController.getInstance();
            GameUser user = controller.getUser(message.getId());

            if (user == null) return;

            user.setSuspended(false);

            int tableId = user.getTableId();
            if (tableId < 1) {
                controller.sendNetworkMessage(user, GameCommands.AWAKE, null);
            } else {
                String response = null;

                Table table = controller.getTable(tableId);

                UserStateResponse state = Quick_Start_Game.sendReconnectTable(user, table, false);

                if(state != null){
                    response = NetworkMessage.getGson().toJson(state);
                }

                controller.sendNetworkMessage(user, GameCommands.AWAKE, response);
            }

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }
}
