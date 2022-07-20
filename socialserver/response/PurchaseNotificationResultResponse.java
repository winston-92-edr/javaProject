package com.mynet.socialserver.response;

public class PurchaseNotificationResultResponse {
    private final String receiverName;
    private final boolean result;

    public PurchaseNotificationResultResponse(String receiverName, boolean result) {
        this.receiverName = receiverName;
        this.result = result;
    }
}
