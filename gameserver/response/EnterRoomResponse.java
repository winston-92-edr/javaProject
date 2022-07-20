package com.mynet.gameserver.response;

import com.mynet.gameserver.enums.RoomStatus;

public class EnterRoomResponse {
    private final int roomId;
    private final RoomStatus status;

    public EnterRoomResponse(Builder builder) {
        this.roomId = builder.roomId;
        this.status = builder.status;
    }

    @Override
    public String toString() {
        return "EnterRoomResponse{" +
                "roomId=" + roomId +
                ", status=" + status +
                '}';
    }

    public static class Builder {
        private int roomId;
        private RoomStatus status;

        public Builder setStatus(RoomStatus status) {
            this.status = status;
            return this;
        }

        public Builder setRoomId(int roomId) {
            this.roomId = roomId;
            return this;
        }

        public EnterRoomResponse build(){
            return new EnterRoomResponse(this);
        }
    }
}
