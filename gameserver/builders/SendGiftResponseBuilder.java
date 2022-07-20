package com.mynet.gameserver.builders;

import com.mynet.gameserver.response.ReceiveGiftResponse;

public class SendGiftResponseBuilder {
    private String senderId;
    private String senderName;
    private String receiverId;
    private long senderMoney;
    private String giftId;
    private int giftType;
    private boolean permanent;

    public SendGiftResponseBuilder setSenderId(String senderId) {
        this.senderId = senderId;
        return this;
    }

    public SendGiftResponseBuilder setSenderName(String senderName) {
        this.senderName = senderName;
        return this;
    }

    public SendGiftResponseBuilder setReceiverId(String receiverId) {
        this.receiverId = receiverId;
        return this;
    }

    public SendGiftResponseBuilder setSenderMoney(long senderMoney) {
        this.senderMoney = senderMoney;
        return this;
    }

    public SendGiftResponseBuilder setGiftId(String giftId) {
        this.giftId = giftId;
        return this;
    }

    public SendGiftResponseBuilder setGiftType(int giftType) {
        this.giftType = giftType;
        return this;
    }

    public SendGiftResponseBuilder setPermanent(boolean permanent) {
        this.permanent = permanent;
        return this;
    }

    public ReceiveGiftResponse createSendGiftResponse() {
        return new ReceiveGiftResponse(senderId, senderName, receiverId, senderMoney, giftId, giftType, permanent);
    }
}