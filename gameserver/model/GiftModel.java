package com.mynet.gameserver.model;

import java.beans.ConstructorProperties;

public class GiftModel {
    private String giftId ;
    private int price;
    private int type;
    private boolean permanent;

    @ConstructorProperties({"GIFT_ID","PRICE","PAYMENT_TYPE","GIFT_TYPE"})
    public GiftModel(String giftId, int price, int type, int giftType) {
        this.giftId = giftId;
        this.price = price;
        this.type = type;
        this.permanent = giftType == 1;
    }

    public boolean isPermanent() {
        return permanent;
    }

    public void setPermanent(boolean permanent) {
        this.permanent = permanent;
    }

    public int getPrice()
    {
        return this.price;
    }

    public String getGiftId()
    {
        return this.giftId;
    }

    public int getType()
    {
        return type;
    }
}
