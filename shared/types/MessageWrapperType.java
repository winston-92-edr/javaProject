package com.mynet.shared.types;

public enum MessageWrapperType {
    PROXY(0),
    NETWORK(1);

    private final int value;

    MessageWrapperType(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }
}
