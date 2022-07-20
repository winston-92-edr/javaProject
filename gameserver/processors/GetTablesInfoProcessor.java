package com.mynet.gameserver.processors;

import com.mynet.gameserver.GameController;
import com.mynet.gameserver.okey.Table;
import com.mynet.gameserver.room.Room;
import com.mynet.matchserver.GameUser;
import com.mynet.shared.network.GameCommands;
import com.mynet.shared.network.InvalidServerMessage;
import com.mynet.shared.network.MessageProcessor;
import com.mynet.shared.network.NetworkMessage;
import com.mynet.tableservice.service.ServiceTableModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GetTablesInfoProcessor implements MessageProcessor {
    Logger logger = LoggerFactory.getLogger(GetTablesInfoProcessor.class);

    @Override
    public void process(NetworkMessage message) throws InvalidServerMessage {
        try {
            GameController controller = GameController.getInstance();
            GameUser user = controller.getUser(message.getId());

            StringBuilder info = new StringBuilder();

            List<Table> tables = controller.getTables();
            Collections.sort(tables, (table1, table2) -> {
                int uc1 = getTableOrderForSorting(table1);
                int uc2 = getTableOrderForSorting(table2);

                return Integer.compare(uc2, uc1);
            });

            int fullTable = 0;
            int emptyTable = 0;

            for(int i = 0; i < tables.size(); i++) {
                Table table = tables.get(i);
                if (!table.isVisibleInList()) {
                    continue;
                }
                int tableId = table.getTableId();

                if(table.getUserCount() == 4 ) {
                    fullTable++;
                    if (fullTable >= 24) {
                        if (emptyTable > 8) {
                            break;
                        }
                        continue;
                    }
                } else if (table.getUserCount() == 0) {
                    emptyTable ++;
                }

                if (info.length() > 0)
                {
                    info.append(";");
                }


                info.append(tableId).append(",")
                        .append(table.getGamersFuidsNamesString()).append(",")
                        .append(table.getPotValue()).append(",")
                        .append(table.getRoomId()).append(",")
                        .append(table.getIsPartner())
                        .append(controller.getNodeId());

            }

            controller.sendNetworkMessage(user, GameCommands.TABLES_INFO, info.toString());
        }catch(Exception ex){
            logger.error(ex.getMessage(), ex);
        }
    }

    private int getTableOrderForSorting(Table table) {
        int uc = table.getUserCount();
        uc += 2;
        if (uc == 6) { // full
            if (table.getBotCount() > 0) {
                uc = 2; // full with bots
            } else {
                uc = 0; // full
            }
        } else if (uc == 2) { // empty
            uc = 1;
        }
        return uc;
    }
}
