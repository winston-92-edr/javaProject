package com.mynet.gameserver.response;

import com.google.gson.annotations.SerializedName;
import com.mynet.gameserver.enums.GameStatus;
import com.mynet.gameserver.model.ThrownCardsModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TableInfoResponse {
    boolean throwCard;
    long tId;
    long potValue;
    long remainingTimeNextTurn;
    int side;
    int gameTurn;
    boolean doubleStatus;
    boolean firstTurn;
    int remainingCardsCount;
    ArrayList<ThrownCardsModel> thrownCards;
    List<String> hand;
    String indicator;
    List<Boolean> doubles;
    int dealer;
    GameStatus gameStatus;
    long bet;
    boolean freePlay;

    public TableInfoResponse(boolean throwCard, long tId, long potValue, long remainingTimeNextTurn, int side, int gameTurn, boolean doubleStatus, boolean firstTurn, int remainingCardsCount, ArrayList<ThrownCardsModel> thrownCards, List<String> hand, String indicator, List<Boolean> doubles, int dealer, GameStatus gameStatus, long bet, boolean freePlay) {
        this.throwCard = throwCard;
        this.tId = tId;
        this.potValue = potValue;
        this.remainingTimeNextTurn = remainingTimeNextTurn;
        this.side = side;
        this.gameTurn = gameTurn;
        this.doubleStatus = doubleStatus;
        this.firstTurn = firstTurn;
        this.remainingCardsCount = remainingCardsCount;
        this.thrownCards = thrownCards;
        this.hand = hand;
        this.indicator = indicator;
        this.doubles = doubles;
        this.dealer = dealer;
        this.gameStatus = gameStatus;
        this.bet = bet;
        this.freePlay = freePlay;
    }

    @Override
    public String toString() {
        return "TableInfoResponse{" +
                "throwCard=" + throwCard +
                ", tId=" + tId +
                ", potValue=" + potValue +
                ", remainingTimeNextTurn=" + remainingTimeNextTurn +
                ", side=" + side +
                ", gameTurn=" + gameTurn +
                ", doubleStatus=" + doubleStatus +
                ", firstTurn=" + firstTurn +
                ", remainingCardsCount=" + remainingCardsCount +
                ", thrownCards=" + thrownCards +
                ", hand=" + hand +
                ", indicator='" + indicator + '\'' +
                ", doubles=" + doubles +
                ", dealer=" + dealer +
                '}';
    }
}
