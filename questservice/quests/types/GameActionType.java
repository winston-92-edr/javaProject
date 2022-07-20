package com.mynet.questservice.quests.types;

public enum GameActionType {
    GO_DOUBLE(1);

    GameActionType(int value) {
        this.value = value;
    }

    int value;

    public int getValue() {
        return value;
    }
}
