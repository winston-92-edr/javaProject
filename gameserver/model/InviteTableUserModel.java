package com.mynet.gameserver.model;

public class InviteTableUserModel {
    private String id;
    private boolean vip;
    private int side;


    public InviteTableUserModel(String id, boolean vip, int side) {
        this.id = id;
        this.vip = vip;
        this.side = side;
    }

    public InviteTableUserModel(String id, int side) {
        this.id = id;
        this.side = side;
    }
}
