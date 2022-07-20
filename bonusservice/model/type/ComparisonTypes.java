package com.mynet.bonusservice.model.type;

import java.util.LinkedHashMap;
import java.util.Map;

public enum ComparisonTypes {
    EQUAL(1),
    LESS(2),
    GREATER(3);

    int value;

    ComparisonTypes(int value) {
        this.value = value;
    }

    private static final Map<Integer, ComparisonTypes> BY_CODE_MAP = new LinkedHashMap<>();

    static {
        for (ComparisonTypes rae : ComparisonTypes.values()) {
            BY_CODE_MAP.put(rae.value, rae);
        }
    }

    public static ComparisonTypes forCode(int value) {
        return BY_CODE_MAP.get(value);
    }

    public int getValue() { return value;}
}
