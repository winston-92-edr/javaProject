package com.mynet.gameserver.model;

import com.mynet.gameserver.room.RoomType;

import java.util.ArrayList;

public class CreateRoomsModel {
    private ArrayList<RoomType> rooms;

    public CreateRoomsModel(ArrayList<RoomType> rooms) {
        this.rooms = rooms;
    }

    public ArrayList<RoomType> getRooms() {
        return rooms;
    }

    public void setRooms(ArrayList<RoomType> rooms) {
        this.rooms = rooms;
    }
}
