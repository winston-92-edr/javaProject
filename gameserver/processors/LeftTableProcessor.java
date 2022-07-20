package com.mynet.gameserver.processors;

import com.mynet.gameserver.GameController;
import com.mynet.gameserver.okey.Table;
import com.mynet.matchserver.GameUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.mynet.shared.network.InvalidServerMessage;
import com.mynet.shared.network.MessageProcessor;
import com.mynet.shared.network.NetworkMessage;
import com.mynet.shared.resource.CacheController;

public class LeftTableProcessor implements MessageProcessor {
    private static Logger logger = LoggerFactory.getLogger(LeftTableProcessor.class);
    @Override
    public void process(NetworkMessage message) throws InvalidServerMessage {

        GameUser user = GameController.getInstance().getUser(message.getId());
        String userRequestedTableId = message.getData();

        try {
            int tableId = user.getTableId();
            if (tableId == -1) {
                logger.warn("LEFT_TABLE | USER TABLE ID IS -1, TABLE REQUESTED: " + userRequestedTableId);
            }else if(Integer.parseInt(userRequestedTableId) != tableId){
                logger.warn(String.format("LEFT_TABLE | USER TABLE ID: %d doesnt match with table id: %s", tableId, userRequestedTableId));
            }
            Table table = GameController.getInstance().getTable(userRequestedTableId);
            if (table != null) {
                if (table.containsUser(user)) {
                    CacheController.getInstance().removeUserTableID(user.getfuid());
                    table.removeUser(user, true);
                }else{
                    logger.warn(String.format("USER:%s is NOT IN TABLE WHILE LEFT TABLE: %s", message.getId(), userRequestedTableId));
                }
            }else{
                logger.warn(String.format("TABLE IS NULL FOR USER: %s | TABLE: %s", message.getId(), userRequestedTableId));

            }
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
    }
}
