package com.mynet.shared.response;

import com.mynet.matchserver.GameUser;

public class NotifyForAudienceResponse {

    private String name;
    private long money;
    private int vip;
    private String gift;
    private String platform;
    private String id;

    public NotifyForAudienceResponse(GameUser user) {
        this.id = user.getfuid();
        this.name = user.getName();
        this.money = user.getMoney();
        this.vip = user.getIsVipAsInt();
        this.gift = user.getUserGift();
        this.platform = user.getPlatform();
    }

    public String getUserId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public long getMoney() {
        return money;
    }

    public int getVip() {
        return vip;
    }

    public String getGift() {
        return gift;
    }

    public String getPlatform() {
        return platform;
    }
}
