package com.mynet.chatserver.models;

public class ChatUser {
    private String id;
    private int tableId;
    private int proxyId;
    private String muteDate;
    private boolean audience;
    private String name;
    private String ip;
    private int roomId;
    private boolean profanityFilter;

    public ChatUser(String id) {
        this.id = id;
        this.profanityFilter = false;
    }

    public ChatUser(String id, int tableId, int proxyId, String muteDate, boolean audience, String name, String ip, int roomId) {
        this.id = id;
        this.tableId = tableId;
        this.proxyId = proxyId;
        this.muteDate = muteDate;
        this.audience = audience;
        this.name = name;
        this.ip = ip;
        this.roomId = roomId;
        this.profanityFilter = false;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setMuteDate(String muteDate) {
        this.muteDate = muteDate;
    }

    public int getTableId() {
        return tableId;
    }

    public void setTableId(int tableId) {
        this.tableId = tableId;
    }

    public int getProxyId() {
        return proxyId;
    }

    public void setProxyId(int proxyId) {
        this.proxyId = proxyId;
    }

    public String getMuteDate() {
        return muteDate;
    }

    public boolean isAudience() {
        return audience;
    }

    public void setAudience(boolean audience) {
        this.audience = audience;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public boolean isProfanityFilter() {
        return profanityFilter;
    }

    public void setProfanityFilter(boolean profanityFilter) {
        this.profanityFilter = profanityFilter;
    }
}
