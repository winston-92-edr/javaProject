package com.mynet.gameserver.logs.move;

public enum MoveDirection {
    IN(0),
    OUT(1);

    private int value;

    MoveDirection(int i) {
        this.value  = i;
    }

    public int getValue(){
        return this.value;
    }
}
