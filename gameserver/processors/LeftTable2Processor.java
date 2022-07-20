package com.mynet.gameserver.processors;

import com.mynet.gameserver.GameController;
import com.mynet.gameserver.okey.Table;
import com.mynet.matchserver.GameUser;
import com.mynet.shared.network.InvalidServerMessage;
import com.mynet.shared.network.MessageProcessor;
import com.mynet.shared.network.NetworkMessage;
import com.mynet.shared.resource.CacheController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LeftTable2Processor implements MessageProcessor {
    private static Logger logger = LoggerFactory.getLogger(LeftTable2Processor.class);

    @Override
    public void process(NetworkMessage message) throws InvalidServerMessage {
        try {
            GameUser user = GameController.getInstance().getUser(message.getId());

            if(user == null) return;

            int tableId = user.getTableId();

            if (tableId == -1) return;

            Table table = GameController.getInstance().getTable(tableId);

            if (table != null) {
                if (table.containsUser(user)) {
                    CacheController.getInstance().removeUserTableID(user.getfuid());
                    table.sendUserSitTableSpecialEvent(user,"LEFT_TABLE", "Game Start Fail", false);
                    table.removeUser2(user, true);
                }
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
    }
}
