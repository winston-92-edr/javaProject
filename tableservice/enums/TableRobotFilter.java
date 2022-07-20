package com.mynet.tableservice.enums;

import com.mynet.socialserver.enums.FriendStatus;

import java.util.LinkedHashMap;
import java.util.Map;

public enum TableRobotFilter {
    ROBOT(0),
    NOT_ROBOT(1),
    ALL(2);

    private int value;

    TableRobotFilter(int i) { this.value = i; }

    public int getValue() { return value; }

    private static final Map<Integer, TableRobotFilter> BY_CODE_MAP = new LinkedHashMap<>();

    static {
        for (TableRobotFilter rae : TableRobotFilter.values()) {
            BY_CODE_MAP.put(rae.getValue(), rae);
        }
    }

    public static TableRobotFilter forCode(int value) {
        return BY_CODE_MAP.get(value);
    }
}
