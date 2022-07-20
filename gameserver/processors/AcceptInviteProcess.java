package com.mynet.gameserver.processors;

import com.mynet.gameserver.GameController;
import com.mynet.gameserver.actions.JoinInviteTableAction;
import com.mynet.gameserver.okey.Table;
import com.mynet.matchserver.GameUser;
import com.mynet.shared.network.InvalidServerMessage;
import com.mynet.shared.network.MessageProcessor;
import com.mynet.shared.network.NetworkMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.StringTokenizer;

public class AcceptInviteProcess implements MessageProcessor {
    private static Logger logger = LoggerFactory.getLogger(AcceptInviteProcess.class);

    @Override
    public void process(NetworkMessage message) throws InvalidServerMessage {

        GameController controller = GameController.getInstance();
        GameUser user = controller.getUser(message.getId());
        try {

            StringTokenizer str = new StringTokenizer(message.getData(), ";");
            int roomId = Integer.parseInt(str.nextToken());
            String tableId = str.nextToken();
            Table tbl = controller.getTable(tableId);
            if (tbl != null) {
                tbl.addTableAction(new JoinInviteTableAction(tbl, user, roomId));
            }

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }
}
