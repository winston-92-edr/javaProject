package com.mynet.gameserver.request;

public class EnterRoomRequest {
    public final int roomId;

    public EnterRoomRequest(int roomId) {
        this.roomId = roomId;
    }

    public int getRoomId() {
        return roomId;
    }

    @Override
    public String toString() {
        return "EnterRoomRequest{" +
                "roomId=" + roomId +
                '}';
    }
}
