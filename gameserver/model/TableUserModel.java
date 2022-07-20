package com.mynet.gameserver.model;

public class TableUserModel {
    boolean bot;
    boolean vip;
    boolean gamer;
    int side;
    String badge;
    String gift;
    String name;
    String id;
    String platform;
    long money;
    boolean doubleStatus;

    public TableUserModel(boolean bot, boolean vip, int side, String name, String id, String badge, String gift, String platform, long money, boolean gamer) {
        this.bot = bot;
        this.vip = vip;
        this.side = side;
        this.name = name;
        this.id = id;
        this.badge = badge;
        this.gift = gift;
        this.platform = platform;
        this.money = money;
        this.gamer = gamer;
    }

    public TableUserModel(boolean bot, boolean vip, int side, String name, String id, String badge, String gift, String platform, long money, boolean gamer, boolean doubleStatus) {
        this.bot = bot;
        this.vip = vip;
        this.gamer = gamer;
        this.side = side;
        this.badge = badge;
        this.gift = gift;
        this.name = name;
        this.id = id;
        this.platform = platform;
        this.money = money;
        this.doubleStatus = doubleStatus;
    }
}
