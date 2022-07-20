package com.mynet.gameserver.request;

public class OpenTableRequest {
    private int roomId;
    private boolean paired;

    public int getRoomId() {
        return roomId;
    }

    public boolean isPaired() {
        return paired;
    }
}
