package com.mynet.gameserver.processors;

import com.mynet.gameserver.GameController;
import com.mynet.gameserver.actions.HandleUserAction;
import com.mynet.gameserver.okey.Table;
import com.mynet.matchserver.GameUser;
import com.mynet.shared.network.GameCommands;
import com.mynet.shared.network.InvalidServerMessage;
import com.mynet.shared.network.MessageProcessor;
import com.mynet.shared.network.NetworkMessage;
import com.mynet.shared.types.MessageTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.StringTokenizer;

public class QuickPlayProcessor implements MessageProcessor {
    private static Logger logger = LoggerFactory.getLogger(QuickPlayProcessor.class);

    @Override
    public void process(NetworkMessage message) throws InvalidServerMessage {

        GameController controller = GameController.getInstance();
        GameUser user = controller.getUser(message.getId());
        if(user == null) return;

        try {
            if (user.getTableId() > -1) {
                //TODO:IMPORTANT
                //leftByVolunteer(user.getTableId() + "");
            }

            if (user.getRoomId() != 0) {
                controller.leaveRoom(user);
            }

            int[] tableAndRoomId = controller.getAvailableRoom(user);
            int bestTableId = tableAndRoomId[0];
            int bestRoomId =  tableAndRoomId[1];
            if (bestTableId > -1) {
                controller.sitPlayNowTable(bestTableId, user);
                return;
            }
            int tableId = controller.openTableQuickPlay(bestRoomId + ";" + 0, user);
            if (tableId != -1) {
                controller.sitPlayNowTable(tableId, user);
            } else {
                controller.sendNetworkMessage(user, GameCommands.QUICK_PLAY, "-1");
            }

        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
    }


}
