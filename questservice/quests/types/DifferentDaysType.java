package com.mynet.questservice.quests.types;

public enum DifferentDaysType {
    PLAY_GAME(1),
    LOGIN(2);

    int value;

    DifferentDaysType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
