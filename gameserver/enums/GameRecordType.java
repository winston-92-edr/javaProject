package com.mynet.gameserver.enums;

public enum GameRecordType {
    PULL_CARD_FROM_DECK(1),
    PULL_CARD_FROM_SIDE(2),
    THROW_CARD(3),
    ADD_PER(4),
    ADD_CARD(5),
    SWAP_CARD(6),
    AUTO_PLAY(7),
    AUTO_ADD_CARDS(8),
    PUNISH_PLAYER(9),
    TAKE_BACK(10),
    SIT_TABLE(11),
    REMOVE_USER(12);

    private final int value;

    GameRecordType(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }
}
