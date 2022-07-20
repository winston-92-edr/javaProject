package com.mynet.gameserver.model;

import java.util.List;

public class HandOverErrorModel {
    List<HandOverCardModel> deck;

    public HandOverErrorModel() {
    }

    public HandOverErrorModel(List<HandOverCardModel> deck) {
        this.deck = deck;
    }

    public List<HandOverCardModel> getDeck() {
        return deck;
    }

    public void setDeck(List<HandOverCardModel> deck) {
        this.deck = deck;
    }
}
