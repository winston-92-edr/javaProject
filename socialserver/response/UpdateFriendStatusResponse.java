package com.mynet.socialserver.response;

public class UpdateFriendStatusResponse {
    String id;
    boolean online;

    public UpdateFriendStatusResponse(String id, boolean online) {
        this.id = id;
        this.online = online;
    }
}
