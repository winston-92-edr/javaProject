package com.mynet.shared.types;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

public enum SettingsTypes {
    GO_PROFILE(0),
    GIFT_ALERT(1),
    PRIVATE_CHAT(2),
    SOUND(3),
    INVITE(4);

    private final int value;

    public int getValue() {
        return value;
    }

    SettingsTypes(int value) {
        this.value = value;
    }

    private static final Map<Integer, SettingsTypes> BY_CODE_MAP = new LinkedHashMap<>();
    static {
        for (SettingsTypes rae : SettingsTypes.values()) {
            BY_CODE_MAP.put(rae.value, rae);
        }
    }

    public static SettingsTypes forCode(int value) {
        return BY_CODE_MAP.get(value);
    }
}
