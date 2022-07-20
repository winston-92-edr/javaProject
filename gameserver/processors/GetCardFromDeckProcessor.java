package com.mynet.gameserver.processors;

import com.mynet.gameserver.okey.Table;
import com.mynet.gameserver.GameController;
import com.mynet.gameserver.actions.GetCardFromDeckAction;
import com.mynet.gameserver.enums.GameStatus;
import com.mynet.gameserver.request.GetCardFromDeckRequest;
import com.mynet.matchserver.GameUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.mynet.proxyserver.network.StringUtil;
import com.mynet.shared.network.InvalidServerMessage;
import com.mynet.shared.network.MessageProcessor;
import com.mynet.shared.network.NetworkMessage;

public class GetCardFromDeckProcessor implements MessageProcessor {
    private static Logger logger = LoggerFactory.getLogger(GetCardFromDeckProcessor.class);

    @Override
    public void process(NetworkMessage message) throws InvalidServerMessage {

        GameController controller = GameController.getInstance();
        GameUser user = controller.getUser(message.getId());
        if(user == null) return;

        try {
            GetCardFromDeckRequest request = NetworkMessage.getGson().fromJson(message.getData(),GetCardFromDeckRequest.class);
            int tableId = user.getTableId();
            if(tableId == -1){
                logger.warn("getCardFromDeck => user tableId is -1, tableId: " + tableId);
                return;
            }

            Table table = controller.getTable(tableId);
            if (table == null) {
                logger.warn("getCardFromDeck => tableId is " + tableId + " but table is null " + message.getData());
                return;
            }

            if (table.getGameStatus() != GameStatus.PLAYING) {
                return;
            }

            boolean isFromDeck = request.getIsFromDeck() == 1;
            int side = user.getSide();

            GetCardFromDeckAction deckAction = new GetCardFromDeckAction(table, user, isFromDeck, side, false);
            table.addGameAction(deckAction);

        } catch (Exception ex) {
            logger.error("getCardFromDeck err:  " + message.getData() + " / " + user.getPlatform(), ex);
        }
    }
}
