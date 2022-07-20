package com.mynet.gameserver.enums;

import java.util.LinkedHashMap;
import java.util.Map;

public enum RoomStatus {
    VALID(1),
    MONEY_LIMIT(2),
    NOT_VIP(3),
    ROOM_LIMIT(4),
    NOT_VALID(5),
    LOW_BET(6),
    ERROR(7);

    private final int value;

    RoomStatus(int value) {
        this.value = value;
    }
    private static final Map<Integer, RoomStatus> BY_CODE_MAP = new LinkedHashMap<>();

    static {
        for (RoomStatus rae : RoomStatus.values()) {
            BY_CODE_MAP.put(rae.value, rae);
        }
    }

    public int getValue() {
        return value;
    }

    public static RoomStatus forCode(int value) {
        return BY_CODE_MAP.get(value);
    }
}
