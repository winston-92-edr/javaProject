package com.mynet.gameserver.processors;

import com.mynet.gameserver.GameController;
import com.mynet.gameserver.actions.Quick_Start_Game;
import com.mynet.gameserver.enums.GameStatus;
import com.mynet.gameserver.model.RemoveGameUserModel;
import com.mynet.gameserver.okey.Table;
import com.mynet.gameserver.response.ExtraTimeResponse;
import com.mynet.gameserver.room.Room;
import com.mynet.matchserver.GameUser;
import com.mynet.proxyserver.model.RemovedUserModel;
import com.mynet.shared.network.GameCommands;
import com.mynet.shared.network.InvalidServerMessage;
import com.mynet.shared.network.MessageProcessor;
import com.mynet.shared.network.NetworkMessage;
import com.mynet.shared.resource.CacheController;
import com.mynet.shared.response.UserStateResponse;
import com.mynet.shared.types.ServerType;
import com.mynet.shared.user.User;
import com.mynet.shared.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SendStateProcessor implements MessageProcessor {
    Logger logger = LoggerFactory.getLogger(SendStateProcessor.class);

    @Override
    public void process(NetworkMessage message) throws InvalidServerMessage {
        try {

            GameController controller = GameController.getInstance();
            GameUser user = controller.getUser(message.getId());
            CacheController cacheController = CacheController.getInstance();

            if (user == null) return;

            user.setSuspended(false);

            String strProxyID = message.getData();
            user.setProxyId(Integer.parseInt(strProxyID));
            int cachedTableId = cacheController.getTableId(user.getId());

            Table userCachedTable = controller.getTable(cachedTableId);
            Table userTable = controller.getTable(user.getTableId());
            Table table = null;
            boolean isAudience;

            cacheController.publishRemoveGameUser(new RemoveGameUserModel(controller.getNodeId(), user.getfuid()));

            if(userTable != null && userTable.containsUser(user)){
                table = userTable;
                isAudience = userTable.isAudienceOrGamer(user.getId()) == 2;

                if(userTable.getTableId() != cachedTableId) {
                    checkAndRemove(userCachedTable, user);
                }

                if(isAudience){
                    table.removeUser(user, true);
                    return;
                }

            }else if(userCachedTable != null && userCachedTable.containsUser(user)){
                table = userCachedTable;
                isAudience = userTable.isAudienceOrGamer(user.getId()) == 2;

                if(userTable.getTableId() != cachedTableId) {
                    checkAndRemove(userTable, user);
                }

                if(isAudience){
                    table.removeUser(user, true);
                    return;
                }
            }

            if(table == null){
                logger.warn(String.format("[RECONNECT] There is no table | user: %s | table: %d | room %d", user.getfuid(), user.getTableId(), user.getRoomId()));
                return;
            }

            user.setRoomId(table.getRoomId());
            cacheController.setTableId(user.getId(), user.getTableId());

            logger.info(String.format("Reconnect for user: %s | table: %d | roomId: %d", user.getfuid(), user.getTableId(), table.getRoomId()));

            UserStateResponse reconnectInfo = Quick_Start_Game.sendReconnectTable(user, table, true);

            if(reconnectInfo != null) {

                if (controller.getServerType().equals(ServerType.GENERIC)) {
                    Utils.setTimeout(() -> controller.sendNetworkMessage(user, GameCommands.SEND_USER_STATE, NetworkMessage.getGson().toJson(reconnectInfo)), 100);
                } else {
                    Utils.setTimeout(() -> controller.sendNetworkMessage(user, GameCommands.SEND_TOURNAMENT_USER_STATE, NetworkMessage.getGson().toJson(reconnectInfo)), 100);
                }
            }


        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    private void checkAndRemove(Table table, GameUser user){
        if(table == null) return;

        GameUser gamer = table.getThisGamer(user.getId());
        if(gamer == null) return;

        table.removeUser(user, false);
    }
}
