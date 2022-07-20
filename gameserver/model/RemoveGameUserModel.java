package com.mynet.gameserver.model;

public class RemoveGameUserModel {
    private int gameNodeId;
    private String userId;

    public RemoveGameUserModel() {
    }

    public RemoveGameUserModel(int gameNodeId, String userId) {
        this.gameNodeId = gameNodeId;
        this.userId = userId;
    }

    public int getGameNodeId() {
        return gameNodeId;
    }

    public void setGameNodeId(int gameNodeId) {
        this.gameNodeId = gameNodeId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
