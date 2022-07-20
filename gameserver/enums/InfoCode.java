package com.mynet.gameserver.enums;

import java.util.LinkedHashMap;
import java.util.Map;

public enum InfoCode {
    DEFAULT(1),
    WEBVIEW(2),
    FORCE_UPDATE(3),
    SYSTEM_MESSAGE(4);

    private final int value;

    private InfoCode(int value) {
        this.value = value;
    }

    private static final Map<Integer, InfoCode> BY_CODE_MAP = new LinkedHashMap<>();

    static {
        for (InfoCode rae : InfoCode.values()) {
            BY_CODE_MAP.put(rae.value, rae);
        }
    }

    public static InfoCode forCode(int value) {
        return BY_CODE_MAP.get(value);
    }

    public int getValue() {
        return value;
    }
}
