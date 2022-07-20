package com.mynet.gameserver.processors;

import com.mynet.gameserver.GameController;
import com.mynet.gameserver.enums.EventDbLogType;
import com.mynet.matchserver.GameUser;
import com.mynet.proxyserver.network.StringUtil;
import com.mynet.shared.network.GameCommands;
import com.mynet.shared.network.InvalidServerMessage;
import com.mynet.shared.network.MessageProcessor;
import com.mynet.shared.network.NetworkMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SitTable2Processor implements MessageProcessor {
    private static Logger logger = LoggerFactory.getLogger(SitTable2Processor.class);

    @Override
    public void process(NetworkMessage message) throws InvalidServerMessage {
        try {
            GameController controller = GameController.getInstance();
            GameUser user = controller.getUser(message.getId());


            if (controller.isInMaintenance()) {
                NetworkMessage cantStartMessage1 = new NetworkMessage(GameCommands.SIT_TABLE_2);
                cantStartMessage1.setData("-1;cantStartGame");
                controller.getNodeToProxy().addServerMessage(cantStartMessage1, user);

                NetworkMessage cantStartMessage2 = new NetworkMessage(GameCommands.CANT_START_GAME);
                cantStartMessage2.setData(controller.getMaintenanceMessage());
                controller.getNodeToProxy().addServerMessage(cantStartMessage2, user);
                return;
            }

            String[] arr = StringUtil.processRawString(message.getData(), ";");
            int side = Integer.parseInt(arr[0]);
            String tableId = arr[1];

            user.setEvent(EventDbLogType.ROOM_CHOOSE);
            NetworkMessage resp = new NetworkMessage(GameCommands.SIT_TABLE_REQUEST);
            controller.sitTableV2WithAction(user, resp, Integer.parseInt(tableId), user.getRoomId(), side, false);

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }
}
