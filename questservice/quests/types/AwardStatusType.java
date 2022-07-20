package com.mynet.questservice.quests.types;

public enum AwardStatusType {
    AVAILABLE(1),
    CLAIMED(2),
    NOT_AVAILABLE(3);

    int value;

    AwardStatusType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
