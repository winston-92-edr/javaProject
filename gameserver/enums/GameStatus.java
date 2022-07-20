package com.mynet.gameserver.enums;

import java.util.LinkedHashMap;
import java.util.Map;

public enum GameStatus {
    PLAYING(0),
    STOPPED(1),
    NOTSTARTED(2),
    WAITING_FOR_USER(3),
    CANT_START(4),
    GET_READY(5);

    private final int status;
    private static final Map<Integer, GameStatus> BY_CODE_MAP = new LinkedHashMap<>();

    private GameStatus(int status)
    {
        this.status = status;
    }

    static {
        for (GameStatus rae : GameStatus.values()) {
            BY_CODE_MAP.put(rae.getValue(), rae);
        }
    }

    public int getValue()
    {
        return this.status;
    }

    public static GameStatus forCode(int value) {
        return BY_CODE_MAP.get(value);
    }
}
