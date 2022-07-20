package com.mynet.shared.types;

public enum ClaimAwardType {
    TOURNAMENT(0),
    NEW_USER_STEP_AWARD(1);

    private final int value;

    ClaimAwardType(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
