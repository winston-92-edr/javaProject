package com.mynet.gameserver.enums;

public enum Bucket {
    FIRST(0),
    SECOND(1),
    COUNT(2);

    private int value;

    Bucket(int i) {
        this.value  = i;
    }

    public int getValue(){
        return this.value;
    }

    @Override
    public String toString() {
        return getValue() == 0 ? "FIRST" : "SECOND";
    }
}
