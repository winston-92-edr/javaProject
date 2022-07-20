package com.mynet.shared.request;

public class OpenTable2Request {
    int roomId;
    int partner;

    public OpenTable2Request(int roomId, int partner) {
        this.roomId = roomId;
        this.partner = partner;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public void setPartner(int partner) {
        this.partner = partner;
    }

    public int getRoomId() {
        return roomId;
    }

    public int getPartner() {
        return partner;
    }
}
