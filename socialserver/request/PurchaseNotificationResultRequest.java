package com.mynet.socialserver.request;

public class PurchaseNotificationResultRequest {
    private String receiverName;
    private String senderId;
    private boolean result;

    public String getReceiverName() {
        return receiverName;
    }

    public String getSenderId() {
        return senderId;
    }

    public boolean isResult() {
        return result;
    }
}
