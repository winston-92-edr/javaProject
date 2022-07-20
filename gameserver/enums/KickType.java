package com.mynet.gameserver.enums;

import java.util.LinkedHashMap;
import java.util.Map;

public enum KickType {
    BACKGROUND(1),
    GO_TO_LOBBY(2),
    NOT_ENOUGH_MONEY(3),
    ADMIN(4),
    CLOSED_TABLE(5),
    AUTO_PLAY(7);

    private final int value;

    private static final Map<Integer, KickType> BY_CODE_MAP = new LinkedHashMap<>();

    static {
        for (KickType rae : KickType.values()) {
            BY_CODE_MAP.put(rae.getValue(), rae);
        }
    }

    KickType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static KickType forCode(int value) {
        return BY_CODE_MAP.get(value);
    }
}
