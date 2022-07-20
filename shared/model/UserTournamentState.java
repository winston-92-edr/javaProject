package com.mynet.shared.model;

public enum UserTournamentState {
    ACTIVE(0),
    SUCCESS(1),
    FAILED(2);

    private final int status;

    UserTournamentState(int status) {
        this.status = status;
    }

    public int getValue()
    {
        return this.status;
    }
}
