package com.mynet.gameserver.model;

public class AvailableTableModel {
    private int gameServerId;
    private int roomId;
    private int tableId;
    private int side;

    public AvailableTableModel(int gameServerId, int roomId, int tableId, int side) {
        this.gameServerId = gameServerId;
        this.roomId = roomId;
        this.tableId = tableId;
        this.side = side;
    }

    public int getGameServerId() {
        return gameServerId;
    }

    public void setGameServerId(int gameServerId) {
        this.gameServerId = gameServerId;
    }

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

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
}
