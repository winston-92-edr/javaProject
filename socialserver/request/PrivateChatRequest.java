package com.mynet.socialserver.request;

public class PrivateChatRequest {
    private String receiverId;
    private String messageId;
    private String message;

    public String getReceiverId() {
        return receiverId;
    }

    public String getMessageId() {
        return messageId;
    }

    public String getMessage() {
        return message;
    }
}
