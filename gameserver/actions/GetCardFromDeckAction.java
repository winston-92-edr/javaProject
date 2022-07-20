package com.mynet.gameserver.actions;

import com.mynet.gameserver.builders.GetCardFromDeckResponseBuilder;
import com.mynet.gameserver.enums.ErrorCode;
import com.mynet.gameserver.logs.move.MoveDestination;
import com.mynet.gameserver.logs.move.MoveDirection;
import com.mynet.gameserver.okey.CardMap;
import com.mynet.gameserver.okey.Table;
import com.mynet.gameserver.enums.GameStatus;
import com.mynet.gameserver.okey.CardHandler;
import com.mynet.gameserver.okey.OkeyCard;
import com.mynet.gameserver.response.GetCardFromDeckResponse;
import com.mynet.matchserver.GameUser;
import com.mynet.shared.network.NetworkMessage;
import com.mynet.shared.response.ErrorResponse;
import com.mynet.shared.builders.ErrorResponseBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GetCardFromDeckAction extends TableAction {
    private static Logger logger = LoggerFactory.getLogger(GetCardFromDeckAction.class);

    private final boolean isFromDeck;
    private final int side;
    private final boolean autoPlay;

    public GetCardFromDeckAction(Table table, GameUser user, boolean isFromDeck, int side, boolean autoPlay) {
        super(table, user);
        this.isFromDeck = isFromDeck;
        this.side = side;
        this.autoPlay = autoPlay;
    }

    @Override
    public boolean process() {
        try {
            if (table.getGameStatus() != GameStatus.PLAYING) {
                return true;
            }

            if (table.getCurrentUser() != user) {
                ErrorResponse response = new ErrorResponseBuilder().setCode(ErrorCode.GET_CARD_NOT_YOUR_TURN).createErrorResponse();
                table.sendErrorMessage(user, NetworkMessage.getGson().toJson(response));
                return true;
            }

            CardHandler chandler = table.getCardHandler();
            int size = chandler.getHandCardSize(table.getGameTurn());

            if (size > 14) {
                ErrorResponse response = new ErrorResponseBuilder().setCode(ErrorCode.MORE_THAN_14).createErrorResponse();
                table.sendErrorMessage(user, NetworkMessage.getGson().toJson(response));
                return true;
            }

            if (table == null) return false;

            table.setLastFromDeck(side);
            CardHandler ch = table.getCardHandler();
            OkeyCard crd;
            if (isFromDeck) {
                crd = ch.removeCardFromDeck();
            } else {
                crd = ch.removeCardFromSideDrop(side);
            }

            int cardNumber = crd.getCardNumber();
            int cardType = crd.getCardType();
            int bucket = crd.getBucket();

            int remainedCount = ch.getCardsCountInDeck();
            String str = cardNumber + "," + cardType + "," + bucket;

            int carId = table.getPile().getCardId(str);
            OkeyCard card = new OkeyCard(carId, cardNumber, cardType, bucket);
            ch.addCardToSide( side, card );

            if(!autoPlay) {
                user.hasPlayed();
            }

            GetCardFromDeckResponse response = new GetCardFromDeckResponseBuilder()
                    .setTableId(table.getTableId())
                    .setSide(side)
                    .setCardId(CardMap.getInstance().getCardId(crd))
                    .setFromDeck(isFromDeck)
                    .setRemainedCount(remainedCount)
                    .setAutoplay(autoPlay)
                    .createGetCardFromDeckResponse();
            table.sendGetCardFromDeck(NetworkMessage.getGson().toJson(response));

            table.addTime();
            int userSide = table.getGamerSide(user.getfuid());
            int fromSide = isFromDeck ? -1 : getPreviousSide(userSide);
            MoveDestination destination = isFromDeck ? MoveDestination.PLAYER : MoveDestination.SIDE;
            table.logMove(fromSide, userSide, card, this.autoPlay, MoveDirection.IN, destination);
            return true;

        } catch (Exception ex) {
            logger.error(" user:" + user.getfuid() + " side:" + side + " isFromDeck:" + isFromDeck + " isTableNULL:" + (table == null)
                    + " msg:" +  ex.getMessage(), ex);
        }
        return false;

    }

    private int getPreviousSide(int side) {
        switch (side) {
            case 0:
                return 3;
            case 1:
                return 2;
            case 3:
                return 1;
            default:
                return 0;
        }
    }

    @Override
    public GameAction getGameAction() {
        return null;
    }

    @Override
    public String getName() {
        return "Get Card";
    }
}
