package com.mynet.shared.logs;

/**
 * Created by ekaya on 15.03.2016.
 */
public enum WinnerDbLogType {
    UPDATE_USER_MONEY_LOG(0),
    LOG_WINNERS(1),
    UPDATE_USER_WINS(2),
    HAND_OVER_LOG(3),
    USER_EVENT(4),
    UPDATE_USER_TICKET_LOG(5),
    ADD_PIGGY_BANK(6);

    private final int value;

    WinnerDbLogType(int value)
    {
        this.value = value;
    }

    public int getValue()
    {
        return this.value;
    }
}
