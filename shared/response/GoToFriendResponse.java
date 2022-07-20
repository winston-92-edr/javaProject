package com.mynet.shared.response;

public class GoToFriendResponse {
    int roomId;
    int tableId;
    int gameNodeId;

    public GoToFriendResponse(int roomId, int tableId, int gameNodeId) {
        this.roomId = roomId;
        this.tableId = tableId;
        this.gameNodeId = gameNodeId;
    }
}
