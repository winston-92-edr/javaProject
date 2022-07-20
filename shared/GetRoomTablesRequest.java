package com.mynet.shared;

public class GetRoomTablesRequest {
    private int roomId;
    private String userId;
    private int proxyId;

    public GetRoomTablesRequest(int roomId, String userId, int proxyId) {
        this.roomId = roomId;
        this.userId = userId;
        this.proxyId = proxyId;
    }

    public int getRoomId() {
        return roomId;
    }

    public String getUserId() {
        return userId;
    }

    public int getProxyId() {
        return proxyId;
    }
}
