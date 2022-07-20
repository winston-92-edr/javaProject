package com.mynet.gameserver.processors;

import com.mynet.gameserver.GameController;
import com.mynet.gameserver.okey.Table;
import com.mynet.matchserver.GameUser;
import com.mynet.proxyserver.network.StringUtil;
import com.mynet.shared.enums.PlayerSide;
import com.mynet.shared.network.GameCommands;
import com.mynet.shared.network.InvalidServerMessage;
import com.mynet.shared.network.MessageProcessor;
import com.mynet.shared.network.NetworkMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SitTableProcessor implements MessageProcessor {
    private static Logger logger = LoggerFactory.getLogger(SitTableProcessor.class);

    @Override
    public void process(NetworkMessage message) throws InvalidServerMessage {
        try {
            GameController controller = GameController.getInstance();
            GameUser user = controller.getUser(message.getId());


            int side;
            int tableId;

            String[] arr = StringUtil.processRawString(message.getData(), ";");
            if (arr.length == 1) {
                try {
                    side = Integer.parseInt(message.getData());
                    tableId = user.getTableId();
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                    return;
                }
            } else {
                try {
                    side = Integer.parseInt(arr[0]);
                    tableId = Integer.parseInt(arr[1]);
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                    return;
                }
            }
            NetworkMessage resp = new NetworkMessage(GameCommands.SIT_TABLE_REQUEST);
            Table table = controller.getTable(tableId);
            controller.sitTableWithAction(user, resp, tableId, table.getRoomId(), PlayerSide.forCode(side), false, PlayerSide.forCode(side).name());


        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }
}
