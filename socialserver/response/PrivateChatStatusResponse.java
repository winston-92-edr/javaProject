package com.mynet.socialserver.response;

public class PrivateChatStatusResponse {
    String messageId;
    boolean status;

    public PrivateChatStatusResponse(String messageId, boolean status) {
        this.messageId = messageId;
        this.status = status;
    }
}
