package com.mynet.questservice.quests.types;

public enum ExpSourceType {
    DAILY(1),
    SEASONAL(2),
    TOURNAMENT(3),
    SINGLE(4),
    PAIRED(5);

    int value;

    ExpSourceType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
