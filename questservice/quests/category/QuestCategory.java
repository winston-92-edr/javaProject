package com.mynet.questservice.quests.category;

import java.util.LinkedHashMap;
import java.util.Map;

public enum QuestCategory {
    END_GAME(0),
    WON_GAME(1),
    MONEY(2),
    GAME_ACTION(3),
    TOURNAMENT(4),
    SOCIAL(5),
    STREAK(6),
    BOT(7),
    DIFFERENT_DAYS(8),
    LOST_GAME(9);

    private final int value;

    QuestCategory(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    private static final Map<Integer, QuestCategory> BY_CODE_MAP = new LinkedHashMap<>();
    static {
        for (QuestCategory rae : QuestCategory.values()) {
            BY_CODE_MAP.put(rae.value, rae);
        }
    }

    public static QuestCategory forCode(int value) {
        return BY_CODE_MAP.get(value);
    }
}

