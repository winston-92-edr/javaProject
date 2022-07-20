package com.mynet.socialserver.response;

public class StartPrivateChatResponse {
    String id;
    boolean start;

    public StartPrivateChatResponse(String id, boolean start) {
        this.id = id;
        this.start = start;
    }
}
