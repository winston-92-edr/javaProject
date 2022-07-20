package com.mynet.questservice.quests.types;

public enum SocialActionType {
    ADD_FRIEND(1),
    SEND_TABLE_MESSAGE(2),
    INVITE_FRIEND_TABLE(3);

    private final int value;

    SocialActionType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
