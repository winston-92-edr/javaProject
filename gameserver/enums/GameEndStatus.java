package com.mynet.gameserver.enums;

public enum GameEndStatus {
    NORMAL(0),
    DROPPED_OKEY(1),
    DOUBLE_FINISH(2),
    PLAYER_LEFT(3);

    private final int status;

    private GameEndStatus(int status)
    {
        this.status = status;
    }

    public int getValue()
    {
        return this.status;
    }
}
