package com.mynet.shared.request;

import com.mynet.gameserver.model.TableFilterModel;
import com.mynet.shared.enums.PlayerSide;

public class EnterTableRequest {

    String friendId;
    Integer tableId;
    PlayerSide side;
    int roomId;
    int gameNodeId;
    boolean invite;
    TableFilterModel filter;

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public void setGameNodeId(int gameNodeId) {
        this.gameNodeId = gameNodeId;
    }

    public int getRoomId() {
        return roomId;
    }

    public int getGameNodeId() {
        return gameNodeId;
    }

    public PlayerSide getSide() {
        return side;
    }

    public void setSide(PlayerSide side) {
        this.side = side;
    }

    public String getFriendId() {
        return friendId;
    }

    public void setFriendId(String friendId) {
        this.friendId = friendId;
    }

    public Integer getTableId() {
        return tableId;
    }

    public void setTableId(Integer tableId) {
        this.tableId = tableId;
    }

    public boolean isInvite() {
        return invite;
    }

    public TableFilterModel getFilter() {
        return filter;
    }

    public void setFilter(TableFilterModel filter) {
        this.filter = filter;
    }

    public EnterTableRequest(String friendId, int tableId, PlayerSide side, int roomId, int gameNodeId) {
        this.friendId = friendId;
        this.tableId = tableId;
        this.side = side;
        this.roomId = roomId;
        this.gameNodeId = gameNodeId;
    }

    @Override
    public String toString() {
        return "EnterTableRequest{" +
                "friendId='" + friendId + '\'' +
                ", tableId=" + tableId +
                ", side=" + side +
                ", roomId=" + roomId +
                ", gameNodeId=" + gameNodeId +
                '}';
    }
}
