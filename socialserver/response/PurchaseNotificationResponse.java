package com.mynet.socialserver.response;

public class PurchaseNotificationResponse {
    private final String senderName;
    private final String giftName;
    private final String orderId;
    private final String senderId;

    public PurchaseNotificationResponse(String senderName, String giftName, String orderId, String senderId) {
        this.senderName = senderName;
        this.giftName = giftName;
        this.orderId = orderId;
        this.senderId = senderId;
    }
}
