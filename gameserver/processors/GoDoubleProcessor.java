package com.mynet.gameserver.processors;

import com.mynet.gameserver.actions.GoDoubleAction;
import com.mynet.gameserver.okey.Table;
import com.mynet.gameserver.GameController;
import com.mynet.matchserver.GameUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.mynet.shared.network.InvalidServerMessage;
import com.mynet.shared.network.MessageProcessor;
import com.mynet.shared.network.NetworkMessage;

public class GoDoubleProcessor implements MessageProcessor {
    private static Logger logger = LoggerFactory.getLogger(GoDoubleProcessor.class);

    @Override
    public void process(NetworkMessage message) throws InvalidServerMessage {
        GameController controller = GameController.getInstance();
        GameUser user = controller.getUser(message.getId());
        try {
            int tableID = user.getTableId();
            Table table = controller.getTable(tableID);
            if(table == null) return;

            table.addGameAction(new GoDoubleAction(table, user));

        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
    }
}
