package com.mynet.gameserver.model;

public class HandOverCardModel {
    String id;
    int x;
    int y;

    public HandOverCardModel(String id, int x, int y) {
        this.id = id;
        this.x = x;
        this.y = y;
    }

    public String getId() {
        return id;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public String serialize(){
        return id + "_" + x + "_" + y + "~";
    }
}
