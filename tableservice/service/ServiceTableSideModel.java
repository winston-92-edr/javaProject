package com.mynet.tableservice.service;

import com.mynet.shared.model.BasicUserModel;

public class ServiceTableSideModel {
    private int side;
    private BasicUserModel user;
    private boolean bot;

    public ServiceTableSideModel(int side, BasicUserModel user, boolean bot) {
        this.side = side;
        this.user = user;
        this.bot = bot;
    }

    public int getSide() {
        return side;
    }

    public void setSide(int side) {
        this.side = side;
    }

    public BasicUserModel getUser() {
        return user;
    }

    public void setUser(BasicUserModel user) {
        this.user = user;
    }

    public boolean isBot() {
        return bot;
    }

    public void setBot(boolean bot) {
        this.bot = bot;
    }

    public boolean isEmpty(){
        return user == null && !bot;
    }
}
