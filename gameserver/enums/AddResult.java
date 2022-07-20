package com.mynet.gameserver.enums;

public enum AddResult {
    SUCCESS(0),
    ALREADYHAS(1),
    WRONGVALUE(2),
    WRONGTYPE(3),
    WRONGORDER(4),
    COUNT(5);

    private int value;

    AddResult(int i) {
        this.value  = i;
    }

    public int getValue(){
        return this.value;
    }

    @Override
    public String toString() {
        String[] arr = { "SUCCESS", "ALREADYHAS", "WRONGVALUE", "WRONGTYPE", "WRONGORDER" };
        return arr[getValue()];
    }
}
