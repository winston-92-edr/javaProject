package com.mynet.shared.response;

public class AddFriendResponse {
    String id;
    String name;
    boolean result;

    public AddFriendResponse(String id, String name, boolean result) {
        this.id = id;
        this.name = name;
        this.result = result;
    }
}
