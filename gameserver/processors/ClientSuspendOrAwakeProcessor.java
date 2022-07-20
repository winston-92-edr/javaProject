package com.mynet.gameserver.processors;

import com.mynet.gameserver.GameController;
import com.mynet.gameserver.actions.Quick_Start_Game;
import com.mynet.gameserver.enums.KickType;
import com.mynet.gameserver.okey.Table;
import com.mynet.gameserver.request.SuspendAwakeRequest;
import com.mynet.gameserver.response.KickResponse;
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

public class ClientSuspendOrAwakeProcessor implements MessageProcessor {
    Logger logger = LoggerFactory.getLogger(ClientSuspendOrAwakeProcessor.class);

    @Override
    public void process(NetworkMessage message) throws InvalidServerMessage {
        try {

            GameController controller = GameController.getInstance();
            GameUser user = controller.getUser(message.getId());

            if (user == null) return;

            SuspendAwakeRequest request = NetworkMessage.CreateMessage(message.getData(), SuspendAwakeRequest.class);
            boolean suspend = request.getSuspend();
            user.setSuspended(suspend);

            if(!suspend){
                int tableId = user.getTableId();
                if (tableId == -1) {
                    controller.sendNetworkMessage(user, GameCommands.KICK_USER_FROM_TABLE, NetworkMessage.getGson().toJson(new KickResponse(KickType.BACKGROUND)));
                } else {

                    Table table = controller.getTable(tableId);

                    UserStateResponse state = Quick_Start_Game.sendReconnectTable(user, table, false);

                    if(state != null) {
                        String response = NetworkMessage.getGson().toJson(state);

                        if (controller.getServerType().equals(ServerType.GENERIC)) {
                            controller.sendNetworkMessage(user, GameCommands.SEND_USER_STATE, response);
                        } else {
                            controller.sendNetworkMessage(user, GameCommands.SEND_TOURNAMENT_USER_STATE, response);
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }
}
