package com.mynet.shared.model;

import java.beans.ConstructorProperties;

public class ZombieUserModel {
    private int id;
    private long userId;
    private int lobbyId;
    private String ip;
    private String platform;
    private int exId;
    private long date;

    @ConstructorProperties({"id", "fuid", "lobby_id", "ip", "platform", "ex_id", "date"})
    public ZombieUserModel(int id, long userId, int lobbyId, String ip, String platform, int exId, long date) {
        this.id = id;
        this.userId = userId;
        this.lobbyId = lobbyId;
        this.ip = ip;
        this.platform = platform;
        this.exId = exId;
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public long getUserId() {
        return userId;
    }

    public int getLobbyId() {
        return lobbyId;
    }

    public String getIp() {
        return ip;
    }

    public String getPlatform() {
        return platform;
    }

    public int getExId() {
        return exId;
    }

    public long getDate() {
        return date;
    }
}
