package com.mynet.gameserver.processors;

import com.mynet.gameserver.GameController;
import com.mynet.gameserver.response.ThrownCardsResponse;
import com.mynet.gameserver.okey.Table;
import com.mynet.matchserver.GameUser;
import com.mynet.shared.network.GameCommands;
import com.mynet.shared.network.InvalidServerMessage;
import com.mynet.shared.network.MessageProcessor;
import com.mynet.shared.network.NetworkMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GetThrownCardsProcessor implements MessageProcessor {
    private static Logger logger = LoggerFactory.getLogger(GetThrownCardsProcessor.class);

    @Override
    public void process(NetworkMessage message) throws InvalidServerMessage {
        GameController controller = GameController.getInstance();
        GameUser user = controller.getUser(message.getId());

        logger.info("THROWN CARDS request >>: " + message.toString());

        try {
            Table table = controller.getTable(user.getTableId());

            if (table != null) {
                int side = table.getGamerSide(user.getId());

                if (side != -1) {
                    boolean doubleStatus = (table.getGameSide(side).getGoDouble() == 1);

                    if(doubleStatus) {
                        ThrownCardsResponse response = new ThrownCardsResponse(table.getThrownCards(), table.getSideCount());
                        controller.sendNetworkMessage(user, GameCommands.GET_THROWN_CARDS, NetworkMessage.getGson().toJson(response));

                        logger.info("THROWN CARDS response >>: " + response.toString());
                    }
                }
            }


        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }
}
