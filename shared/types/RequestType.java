package com.mynet.shared.types;

public enum RequestType {
    SERVER(0),
    SOCIAL(1),
    GAME(2),
    MATCH(3),
    TABLE(4),
    QUEST(5),
    BONUS(6),
    CHAT(7);

    private final int value;

    RequestType(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }
}
