package com.mynet.gameserver.enums;

public enum EventDbLogType {
    QUICK_PLAY(0),
    ROOM_CHOOSE(1),
    CREATE_TABLE(2),
    INVITED_OR_JOIN(3),
    GOTO_FRIEND(4),
    IN_TABLE(5),
    MATCHMAKING_WAITING(6),
    MATCHMAKING_FOUND(7),
    MATCHMAKING_CONFIRM(8),
    MATCHMAKING_CONFIRM_TIMEOUT(9),
    MATCHMAKING_CANCEL(10),
    MATCHMAKING_CANCEL_BY_OP(11),
    PRIVATE_OWN_TABLE(12),
    PRIVATE_TABLE(13);


    private final int value;

    EventDbLogType(int value) {
        this.value = value;
    }

    public int getValue()
    {
        return this.value;
    }
}
