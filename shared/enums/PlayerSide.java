package com.mynet.shared.enums;

import java.util.LinkedHashMap;
import java.util.Map;

public enum PlayerSide {
    QUICK_PLAY(-1),
    JOIN_AUDIENCE(-2),
    DOWN_PLAYER(0),
    UP_PLAYER(1),
    RIGHT_PLAYER(2),
    LEFT_PLAYER(3);

    final int value;

    PlayerSide(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    private static final Map<Integer, PlayerSide> BY_CODE_MAP = new LinkedHashMap<>();

    static {
        for (PlayerSide rae : PlayerSide.values()) {
            BY_CODE_MAP.put(rae.value, rae);
        }
    }

    public static PlayerSide forCode(int value) {
        return BY_CODE_MAP.get(value);
    }
}
