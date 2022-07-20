package com.mynet.questservice.quests.types;

public enum QuestStatusType {
    COMPLETED(1),
    ACTIVE(2),
    NOT_ACTIVE(3);

    int value;

    QuestStatusType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
