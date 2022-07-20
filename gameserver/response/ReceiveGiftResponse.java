package com.mynet.gameserver.response;

import com.mynet.proxyserver.network.StringUtil;

public class ReceiveGiftResponse {
    private final String senderId;
    private final String senderName;
    private final String receiverId;
    private final long senderMoney;
    private final String giftId;
    private final int giftType;
    private final boolean permanent;

    public ReceiveGiftResponse(String senderId, String senderName, String receiverId, long senderMoney, String giftId, int giftType, boolean permanent) {
        this.senderId = senderId;
        this.senderName = senderName;
        this.receiverId = receiverId;
        this.senderMoney = senderMoney;
        this.giftId = giftId;
        this.giftType = giftType;
        this.permanent = permanent;
    }
}
