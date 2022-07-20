package com.mynet.shared.model;

public class BasicUserModel {
    private String id;
    private String name;
    private String platform;
    private long money;
    private boolean isVip;
    private long ticket;

    private int tournamentId;
    private int proxyId;
    private int roomId;

    public BasicUserModel() {
    }

    public BasicUserModel(String id, String name, String platform, long money, boolean isVip) {
        this.id = id;
        this.name = name;
        this.platform = platform;
        this.money = money;
        this.isVip = isVip;
    }

    public BasicUserModel(String id, long money, boolean isVip, long ticket) {
        this.id = id;
        this.money = money;
        this.isVip = isVip;
        this.ticket = ticket;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public long getMoney() {
        return money;
    }

    public void setMoney(long money) {
        this.money = money;
    }

    public boolean isVip() {
        return isVip;
    }

    public void setVip(boolean vip) {
        isVip = vip;
    }

    public void setTournamentId(int tournamentId) {
        this.tournamentId = tournamentId;
    }

    public int getTournamentId() {
        return tournamentId;
    }

    public int getProxyId() {
        return proxyId;
    }

    public void setProxyId(int proxyId) {
        this.proxyId = proxyId;
    }

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public long getTicket() {
        return ticket;
    }

    public void setTicket(long ticket) {
        this.ticket = ticket;
    }

    @Override
    public String toString() {
        return "BasicUserModel{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", platform='" + platform + '\'' +
                ", money=" + money +
                ", isVip=" + isVip +
                ", ticket=" + ticket +
                ", tournamentId=" + tournamentId +
                ", proxyId=" + proxyId +
                ", roomId=" + roomId +
                '}';
    }
}
