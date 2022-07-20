package com.mynet.chatserver.request;

public class ChatEnterTableRequest {
    final private int tableId;
    final private int roomId;
    final private boolean audience;

    public ChatEnterTableRequest(int tableId, int roomId, boolean audience) {
        this.tableId = tableId;
        this.roomId = roomId;
        this.audience = audience;
    }

    public int getTableId() {
        return tableId;
    }

    public int getRoomId() {
        return roomId;
    }

    public boolean isAudience() {
        return audience;
    }
}
