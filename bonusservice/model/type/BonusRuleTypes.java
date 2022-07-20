package com.mynet.bonusservice.model.type;

import java.util.LinkedHashMap;
import java.util.Map;

public enum BonusRuleTypes {
    ML_AMOUNT(1),
    GAME_COUNT(2),
    JOIN_DATE(3),
    TICKET_AMOUNT(4);

    int value;

    BonusRuleTypes(int value) {
        this.value = value;
    }

    private static final Map<Integer, BonusRuleTypes> BY_CODE_MAP = new LinkedHashMap<>();
    static {
        for (BonusRuleTypes rae : BonusRuleTypes.values()) {
            BY_CODE_MAP.put(rae.value, rae);
        }
    }

    public static BonusRuleTypes forCode(int value) {
        return BY_CODE_MAP.get(value);
    }
}