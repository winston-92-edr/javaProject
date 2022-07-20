package com.mynet.shared.request;

public class SitTableRequest {
    private int tableId;
    private int side;
    private int roomId;
    private int gameNodeId;

    public int getTableId() {
        return tableId;
    }

    public void setTableId(int tableId) {
        this.tableId = tableId;
    }

    public int getSide() {
        return side;
    }

    public void setSide(int side) {
        this.side = side;
    }

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public int getGameNodeId() {
        return gameNodeId;
    }

    public void setGameNodeId(int gameNodeId) {
        this.gameNodeId = gameNodeId;
    }
}
