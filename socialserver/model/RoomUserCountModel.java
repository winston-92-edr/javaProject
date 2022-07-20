package com.mynet.socialserver.model;

public class RoomUserCountModel {
    int id;
    int count;

    public RoomUserCountModel(int id, int count) {
        this.id = id;
        this.count = count;
    }

    public int getId() {
        return id;
    }

    public int getCount() {
        return count;
    }
}
