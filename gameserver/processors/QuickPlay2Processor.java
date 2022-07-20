package com.mynet.gameserver.processors;

import com.mynet.gameserver.GameController;
import com.mynet.gameserver.actions.QuickPlayAction;
import com.mynet.gameserver.enums.EventDbLogType;
import com.mynet.gameserver.enums.TableType;
import com.mynet.gameserver.okey.Table;
import com.mynet.gameserver.room.Room;
import com.mynet.matchserver.GameUser;
import com.mynet.shared.network.GameCommands;
import com.mynet.shared.network.InvalidServerMessage;
import com.mynet.shared.network.MessageProcessor;
import com.mynet.shared.network.NetworkMessage;
import com.mynet.shared.resource.CacheController;
import com.mynet.shared.types.GamePlayStatusType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QuickPlay2Processor implements MessageProcessor {
    private static Logger logger = LoggerFactory.getLogger(QuickPlay2Processor.class);

    @Override
    public void process(NetworkMessage message) throws InvalidServerMessage {

        GameController controller = GameController.getInstance();
        GameUser user = controller.getUser(message.getId());
        if(user == null) return;

        String logInfo = "";
        try {

            if (user.getTableId() > 0) {
                logInfo += " tid: " + user.getTableId();
                Table table = controller.getTable(user.getTableId());
                if (table == null) {
                    logInfo += " table is null, fuid:" + user.getfuid();
                    logger.error(logInfo);
                    user.resetTableId();
                } else {
                    table.removeUser(user, true);
                }
            }

            int[] tableAndRoomId = controller.getAvailableRoom(user);
            int bestTableId = tableAndRoomId[0];
            int bestRoomId = tableAndRoomId[1];
            Table table = null;
            if (tableAndRoomId[0] > 0) {
                Room room = controller.getRoom(bestRoomId);
                table = room.getTableWithId(bestTableId);
            }
            if (table == null) {
                int tableId = CacheController.getInstance().incAndGetTableCounter();
                GamePlayStatusType error = controller.createTable(user.getfuid(), tableId, bestRoomId, 0, 4, TableType.PUBLIC,true);
                if (error == GamePlayStatusType.VALID) {
                    table = controller.getTable(tableId);
                } else {
                    logger.error("Error:" + error.getMsg() + " bestTableId:" + bestTableId + " bestRoomId:" + bestRoomId);
                }
            }
            if (table != null) {
                if (table.containsUser(user)) {
                    table.removeUser(user, true);
                }
                user.setEvent(EventDbLogType.QUICK_PLAY);
                table.addTableAction(new QuickPlayAction(table, user, table.getTableId(), -1));
                return;
            }
            logger.error(" bestTableId:" + bestTableId + " bestRoomId:" + bestRoomId);

        } catch (Exception e) {
            logger.error("loginfo:" + logInfo + "" + " error:" + e.getMessage(), e);
        }

        try {
            controller.sendNetworkMessage(user, GameCommands.QUICK_PLAY_2, "-1");
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
    }


}
