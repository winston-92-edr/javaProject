package com.mynet.gameserver.processors;

import com.mynet.gameserver.enums.ErrorCode;
import com.mynet.gameserver.okey.CardMap;
import com.mynet.gameserver.okey.OkeyCard;
import com.mynet.gameserver.okey.Table;
import com.mynet.gameserver.GameController;
import com.mynet.gameserver.actions.HandleUserAction;
import com.mynet.gameserver.request.UserActionRequest;
import com.mynet.matchserver.GameUser;
import com.mynet.shared.network.GameCommands;
import com.mynet.shared.response.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.mynet.shared.network.InvalidServerMessage;
import com.mynet.shared.network.MessageProcessor;
import com.mynet.shared.network.NetworkMessage;

public class UserActionProcessor implements MessageProcessor {
    private static Logger logger = LoggerFactory.getLogger(UserActionProcessor.class);

    @Override
    public void process(NetworkMessage message) throws InvalidServerMessage {

        GameController controller = GameController.getInstance();
        GameUser user = controller.getUser(message.getId());
        if(user == null) return;

        try {

            UserActionRequest request = NetworkMessage.getGson().fromJson(message.getData(), UserActionRequest.class);

            int tableId = user.getTableId();
            String cardId = request.getCardId();
            Table table = controller.getTable(tableId);

            if (table == null || table.getThisGamer(user.getfuid()) == null) {
                controller.sendNetworkMessage(user, GameCommands.ERROR, NetworkMessage.getGson().toJson(new ErrorResponse(ErrorCode.TABLE_NULL)));
                return;
            }

            if(table.getCurrentUser() != user){
                controller.sendNetworkMessage(user, GameCommands.ERROR, NetworkMessage.getGson().toJson(new ErrorResponse(ErrorCode.USER_ACTION_NOT_YOUR_TURN)));
                return;
            }

            OkeyCard card = CardMap.getInstance().getCard(cardId);
            HandleUserAction userAction = new HandleUserAction(table, user, user.getSide(), card.getCardType(), card.getCardNumber(), card.getBucket(), false);
            table.addGameAction(userAction);

        } catch (Exception ex) {
            logger.error("UserAction ERROR: " + message.getData() + " / " + user.getId());
            logger.error(ex.getMessage(), ex);
        }
    }
}
