package com.mynet.socialserver.request;

public class PurchaseNotificationRequest {
    private String receiverName;
    private String receiverId;
    private String orderId;
    private String giftName;

    public String getReceiverName() {
        return receiverName;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getGiftName() {
        return giftName;
    }
}
