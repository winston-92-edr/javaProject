package com.mynet.gameserver.enums;

import java.util.LinkedHashMap;
import java.util.Map;

public enum RemoveUserMessages {
    CLOSED_TABLE(1),
    MATCH_DRAW(2),
    CANT_START(3),
    DEFAULT(4);

    private final int msg;

    RemoveUserMessages(int msg) {
        this.msg = msg;
    }

    public int getMsg() {
        return msg;
    }

    private static final Map<Integer, RemoveUserMessages> BY_CODE_MAP = new LinkedHashMap<>();

    static {
        for (RemoveUserMessages rae : RemoveUserMessages.values()) {
            BY_CODE_MAP.put(rae.getValue(), rae);
        }
    }

    public int getValue()
    {
        return this.msg;
    }

    public static RemoveUserMessages forCode(int value) {
        return BY_CODE_MAP.get(value);
    }
}
