package com.mynet.gameserver.model;

public class TableUserShortModel {
    private final String id;
    private long money;
    private long experience;
    private String name;
    private boolean isVip;

    public TableUserShortModel(String id, long money, long experience, String name, boolean isVip) {
        this.id = id;
        this.money = money;
        this.experience = experience;
        this.name = name;
        this.isVip = isVip;
    }

    public String getId() {
        return id;
    }

    public long getMoney() {
        return money;
    }

    public void setMoney(long money) {
        this.money = money;
    }

    public long getExperience() {
        return experience;
    }

    public void setExperience(long experience) {
        this.experience = experience;
    }

    public boolean isVip() {
        return isVip;
    }

    public String getName() {
        return name;
    }
}
