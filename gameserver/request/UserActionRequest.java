package com.mynet.gameserver.request;

public class UserActionRequest {
    private final int opside;
    private final String cardId;

    public UserActionRequest(int opside, String cardId) {
        this.opside = opside;
        this.cardId = cardId;
    }

    public int getOpside() {
        return opside;
    }

    public String getCardId() {
        return cardId;
    }
}
