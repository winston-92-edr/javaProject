package com.mynet.tableservice.model;

public class QuickPlayRoomModel {
    String name;
    long bet;
    int roomId;

    public QuickPlayRoomModel(String name, long bet, int roomId) {
        this.name = name;
        this.bet = bet;
        this.roomId = roomId;
    }
}
