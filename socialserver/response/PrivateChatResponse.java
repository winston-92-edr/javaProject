package com.mynet.socialserver.response;

public class PrivateChatResponse {
    private final String senderId;
    private final String senderName;
    private final String message;
    private final String messageId;
    private final long date;

    public PrivateChatResponse(String senderId, String senderName, String message, String messageId, long date) {
        this.senderId = senderId;
        this.senderName = senderName;
        this.message = message;
        this.messageId = messageId;
        this.date = date;
    }
}
