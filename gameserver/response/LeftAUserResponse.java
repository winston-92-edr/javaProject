package com.mynet.gameserver.response;

public class LeftAUserResponse {
    String id;
    String name;
    long tId;
    int side;
    boolean audience;
    boolean bot;

    public LeftAUserResponse(String id, String name, long tId, int side, boolean audience, boolean bot) {
        this.id = id;
        this.tId = tId;
        this.side = side;
        this.audience = audience;
        this.name = name;
        this.bot = bot;
    }
}
