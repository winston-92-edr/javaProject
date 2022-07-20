package com.mynet.gameserver.enums;

public enum TableType {
    PUBLIC(0),
    PRIVATE(1);

    private int value;

    TableType(int i) { this.value = i; }

    public int getValue() { return value; }
}
