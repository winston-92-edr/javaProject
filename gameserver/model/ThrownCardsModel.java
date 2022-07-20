package com.mynet.gameserver.model;

import java.util.List;

public class ThrownCardsModel {
    int side;
    List<String> cards;

    public ThrownCardsModel(int side, List<String> cards) {
        this.side = side;
        this.cards = cards;
    }
}
