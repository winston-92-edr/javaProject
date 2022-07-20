package com.mynet.gameserver.builders;

import com.mynet.gameserver.enums.GameStatus;
import com.mynet.gameserver.model.ThrownCardsModel;
import com.mynet.gameserver.response.TableInfoResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SendTableInfoResponseBuilder {
    private boolean throwCard;
    private long tId;
    private long potValue;
    private long remainingTimeNextTurn;
    private int side;
    private int gameTurn;
    private boolean doubleStatus;
    private boolean firstTurn;
    private int remainingCardsCount;
    private List<String> hand;
    ArrayList<ThrownCardsModel> thrownCards;
    private String indicator;
    private List<Boolean> doubles;
    private int dealer;
    private GameStatus gameStatus;
    private long bet;
    private boolean freePlay;


    public SendTableInfoResponseBuilder setThrowCard(boolean throwCard) {
        this.throwCard = throwCard;
        return this;
    }

    public SendTableInfoResponseBuilder settId(long tId) {
        this.tId = tId;
        return this;
    }

    public SendTableInfoResponseBuilder setPotValue(long potValue) {
        this.potValue = potValue;
        return this;
    }

    public SendTableInfoResponseBuilder setRemainingTimeNextTurn(long remainingTimeNextTurn) {
        this.remainingTimeNextTurn = remainingTimeNextTurn;
        return this;
    }

    public SendTableInfoResponseBuilder setSide(int side) {
        this.side = side;
        return this;
    }

    public SendTableInfoResponseBuilder setGameTurn(int gameTurn) {
        this.gameTurn = gameTurn;
        return this;
    }

    public SendTableInfoResponseBuilder setDoubleStatus(boolean doubleStatus) {
        this.doubleStatus = doubleStatus;
        return this;
    }

    public SendTableInfoResponseBuilder setFirstTurn(boolean firstTurn) {
        this.firstTurn = firstTurn;
        return this;
    }

    public SendTableInfoResponseBuilder setRemainingCardsCount(int remainingCardsCount) {
        this.remainingCardsCount = remainingCardsCount;
        return this;
    }

    public SendTableInfoResponseBuilder setThrownCards(ArrayList<ThrownCardsModel> thrownCards) {
        this.thrownCards = thrownCards;
        return this;
    }

    public SendTableInfoResponseBuilder setHand(List<String> hand) {
        this.hand = hand;
        return this;
    }

    public SendTableInfoResponseBuilder setIndicator(String indicator) {
        this.indicator = indicator;
        return this;
    }

    public SendTableInfoResponseBuilder setDoubles(List<Boolean> doubles) {
        this.doubles = doubles;
        return this;
    }

    public SendTableInfoResponseBuilder setDealer(int dealer) {
        this.dealer = dealer;
        return this;
    }

    public SendTableInfoResponseBuilder setGameStatus(GameStatus gameStatus) {
        this.gameStatus = gameStatus;
        return this;
    }

    public SendTableInfoResponseBuilder setBet(long bet) {
        this.bet = bet;
        return this;
    }

    public  SendTableInfoResponseBuilder setFreePlay(boolean freePlay){
        this.freePlay = freePlay;
        return this;
    }

    public TableInfoResponse createSendTableInfoResponse() {
        return new TableInfoResponse(throwCard, tId, potValue, remainingTimeNextTurn, side, gameTurn, doubleStatus, firstTurn, remainingCardsCount, thrownCards, hand, indicator, doubles, dealer, gameStatus, bet, freePlay);
    }

    @Override
    public String toString() {
        return "SendTableInfoResponseBuilder{" +
                "throwCard=" + throwCard +
                ", tId=" + tId +
                ", potValue=" + potValue +
                ", remainingTimeNextTurn=" + remainingTimeNextTurn +
                ", side=" + side +
                ", gameTurn=" + gameTurn +
                ", doubleStatus=" + doubleStatus +
                ", firstTurn=" + firstTurn +
                ", remainingCardsCount=" + remainingCardsCount +
                ", hand=" + hand +
                ", thrownCards=" + thrownCards +
                ", indicator='" + indicator + '\'' +
                ", doubles=" + doubles +
                ", dealer=" + dealer +
                '}';
    }
}