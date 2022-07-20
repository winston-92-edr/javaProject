package com.mynet.questservice.quests.types;

import java.util.LinkedHashMap;
import java.util.Map;

public enum QuestAwardType {
    ML("ml"),
    VIP("vip"),
    TICKET("ticket"),
    PLATINUM("platinum"),
    GOLD("gold"),
    SILVER("silver"),
    BRONZE("bronze"),
    WHEEL("wheel"),
    DOUBLE_XP("doubleXP");

    QuestAwardType(String value) {
        this.value = value;
    }

    private final String value;

    public String getValue() {
        return value;
    }

    private static final Map<String, QuestAwardType> BY_CODE_MAP = new LinkedHashMap<>();
    static {
        for (QuestAwardType rae : QuestAwardType.values()) {
            BY_CODE_MAP.put(rae.value, rae);
        }
    }

    public static QuestAwardType forCode(String value) {
        return BY_CODE_MAP.get(value);
    }
}
