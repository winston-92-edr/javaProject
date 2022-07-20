package com.mynet.gameserver.response;

public class TableChatResponse {
    String id;
    String message;
    String username;
    boolean isAudience;
    long date;

    public TableChatResponse(String id, String message, String username, boolean isAudience) {
        this.id = id;
        this.message = message;
        this.username = username;
        this.isAudience = isAudience;
        this.date = System.currentTimeMillis();
    }
}
