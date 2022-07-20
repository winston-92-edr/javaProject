package com.mynet.questservice.quests.types;

public enum StreakType {
    WINNING(1);

    int value;

    StreakType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
