package com.mynet.shared.logs;

public class PrivateChatThreadLog extends QueueElement{
    private final String sender;
    private final String receiver;
    private final String message;
    private final String messageId;
    private final int status;

    private long date;

    public PrivateChatThreadLog(String sender, String receiver, String message, String messageId, int status) {
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
        this.messageId = messageId;
        this.status = status;

        this.date = System.currentTimeMillis();
    }
}
