package com.mynet.shared.types;

import java.util.LinkedHashMap;
import java.util.Map;

public enum  GameType {
    TOURNAMENT_DUELLO(0),
    TOURNAMENT_CLASSIC(1),
    RIZIKO(2);

    private final int value;

    GameType(int value){
        this.value = value;
    }

    public int getValue(){
        return value;
    }

    private static final Map<Integer, GameType> BY_CODE_MAP = new LinkedHashMap<>();
    static {
        for (GameType rae : GameType.values()) {
            BY_CODE_MAP.put(rae.value, rae);
        }
    }

    public static GameType forCode(int value) {
        return BY_CODE_MAP.get(value);
    }
}
