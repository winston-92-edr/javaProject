package com.mynet.socialserver.model;

public class FriendModel {
     String id;
     int roomId;
     int tableId;
     int lobbyId;

    public FriendModel(String id, int roomId, int tableId, int lobbyId) {
        this.id = id;
        this.roomId = roomId;
        this.tableId = tableId;
        this.lobbyId = lobbyId;
    }
}
