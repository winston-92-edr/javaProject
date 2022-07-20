package com.mynet.tableservice.enums;

import java.util.LinkedHashMap;
import java.util.Map;

public enum TableFullnessFilter {
    FULL(0),
    HALF_FULL(1),
    EMPTY(2),
    ALL(3);

    private int value;

    TableFullnessFilter(int i) { this.value = i; }

    public int getValue() { return value; }

    private static final Map<Integer, TableFullnessFilter> BY_CODE_MAP = new LinkedHashMap<>();

    static {
        for (TableFullnessFilter rae : TableFullnessFilter.values()) {
            BY_CODE_MAP.put(rae.getValue(), rae);
        }
    }

    public static TableFullnessFilter forCode(int value) {
        return BY_CODE_MAP.get(value);
    }
}
