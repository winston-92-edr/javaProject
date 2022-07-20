package com.mynet.gameserver.logs.move;

public enum MoveDestination {
    DECK(0),
    SIDE(1),
    PLAYER(2);

    private int value;

    MoveDestination(int i) {
        this.value  = i;
    }

    public int getValue(){
        return this.value;
    }
}
