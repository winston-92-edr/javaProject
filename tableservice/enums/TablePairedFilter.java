package com.mynet.tableservice.enums;

import com.mynet.socialserver.enums.FriendStatus;

import java.util.LinkedHashMap;
import java.util.Map;

public enum TablePairedFilter {
    PAIRED(0),
    NOT_PAIRED(1),
    ALL(2);

    private int value;

    TablePairedFilter(int i) { this.value = i; }

    public int getValue() { return value; }

    private static final Map<Integer, TablePairedFilter> BY_CODE_MAP = new LinkedHashMap<>();

    static {
        for (TablePairedFilter rae : TablePairedFilter.values()) {
            BY_CODE_MAP.put(rae.getValue(), rae);
        }
    }

    public static TablePairedFilter forCode(int value) {
        return BY_CODE_MAP.get(value);
    }
}
