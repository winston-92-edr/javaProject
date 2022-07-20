package com.mynet.gameserver.actions;

import com.mynet.gameserver.enums.ErrorCode;
import com.mynet.gameserver.okey.Table;
import com.mynet.gameserver.okey.CardHandler;
import com.mynet.matchserver.GameUser;
import com.mynet.shared.builders.ErrorResponseBuilder;
import com.mynet.shared.network.NetworkMessage;
import com.mynet.shared.response.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HandleUserAction extends TableAction {
    private static Logger logger = LoggerFactory.getLogger(HandleUserAction.class);
    private final int opside;
    private final int cardType;
    private final int cardNumber;
    private final int bucketNumber;
    private final boolean autoPlay;

    public HandleUserAction(Table table, GameUser user, int opside, int cardType, int cardNumber, int bucketNumber, boolean autoPlay) {
        super(table, user);
        this.opside = opside;
        this.cardType = cardType;
        this.cardNumber = cardNumber;
        this.bucketNumber = bucketNumber;
        this.autoPlay = autoPlay;
    }

    @Override
    public boolean process() {

        if(table == null){
            logger.warn("Table is null when process user action! user: " + user.getId());
            return true;
        }

        GameUser currentUser = table.getCurrentUser();
        if (currentUser == null ||currentUser != user) {
            ErrorResponse response = new ErrorResponseBuilder().setCode(ErrorCode.USER_ACTION_NOT_YOUR_TURN).createErrorResponse();
            table.sendErrorMessage(user, NetworkMessage.getGson().toJson(response));
            return true;
        }

        int cardId = table.getPile().getCardId(cardNumber + "," + cardType + "," + bucketNumber);
        String fuid = user.getfuid();

        if (table.getThisGamer(fuid) == null && table != null) {
            ErrorResponse response = new ErrorResponseBuilder().setCode(ErrorCode.NOT_YOUR_TABLE).createErrorResponse();
            table.sendErrorMessage(user, NetworkMessage.getGson().toJson(response));
            return true;
        }

        CardHandler chandler = table.getCardHandler();

        String hand = chandler.getSideHand(opside);

        String cardString = cardType + ":" + cardNumber + ":" + bucketNumber;
        if (!hand.contains(cardString)) {
            ErrorResponse response = new ErrorResponseBuilder().setCode(ErrorCode.USER_ACTION_NOT_IN_USER_HAND).createErrorResponse();
            table.sendErrorMessage(user, NetworkMessage.getGson().toJson(response));
            return true;
        }

        int size = chandler.getHandCardSize(opside);

        if (size < 15) {
            ErrorResponse response = new ErrorResponseBuilder().setCode(ErrorCode.USER_ACTION_LESS_THAN_14).createErrorResponse();
            table.sendErrorMessage(user, NetworkMessage.getGson().toJson(response));
            return true;
        }

        if (!autoPlay){
            user.hasPlayed();
        }

        table.incUserActionCount();
        table.sendUserAction(opside, cardType, cardNumber, bucketNumber, cardId, autoPlay);
        return true;
    }

    @Override
    public GameAction getGameAction() {
        return null;
    }

    @Override
    public String getName() {
        return "Handle User";
    }
}
