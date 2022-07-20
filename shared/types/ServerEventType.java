package com.mynet.shared.types;

import java.util.LinkedHashMap;
import java.util.Map;

public enum ServerEventType {
    MONEY_UPDATE("MONEY_UPDATE"),
    TICKET_UPDATE("TICKET_UPDATE"),
    VIP_UPDATE("VIP_UPDATE"),
    BAN_USER("BAN_USER"),
    MUTE_USER("MUTE_USER");

    private final String value;

    ServerEventType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    private static final Map<String, ServerEventType> BY_CODE_MAP = new LinkedHashMap<>();
    static {
        for (ServerEventType rae : ServerEventType.values()) {
            BY_CODE_MAP.put(rae.value, rae);
        }
    }

    public static ServerEventType forCode(String value) {
        return BY_CODE_MAP.get(value);
    }
}
