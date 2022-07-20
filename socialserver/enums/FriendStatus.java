package com.mynet.socialserver.enums;

import com.mynet.gameserver.enums.KickType;

import java.util.LinkedHashMap;
import java.util.Map;

public enum FriendStatus {
    NOT_FRIEND(0),
    NOT_FACEBOOK_FRIEND(1);

    private int value;

    FriendStatus(int i) {
        this.value  = i;
    }

    public int getValue(){
        return this.value;
    }

    private static final Map<Integer, FriendStatus> BY_CODE_MAP = new LinkedHashMap<>();

    static {
        for (FriendStatus rae : FriendStatus.values()) {
            BY_CODE_MAP.put(rae.getValue(), rae);
        }
    }

    public static FriendStatus forCode(int value) {
        return BY_CODE_MAP.get(value);
    }
}
