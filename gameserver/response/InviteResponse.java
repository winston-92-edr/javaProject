package com.mynet.gameserver.response;

import com.mynet.gameserver.model.InviteTableUserModel;

import java.util.List;

public class InviteResponse {
    private final int tableId;
    private final int gameNode;
    private final String name;
    private final String senderId;
    private final int roomId;
    private final boolean partner;
    private final long bet;
    private final long pot;
    private final boolean vip;
    private final List<InviteTableUserModel> users;

    public InviteResponse(int tableId, int gameNode, String name, String senderId, int roomId, boolean partner, long bet, long pot, List<InviteTableUserModel> users, boolean vip) {
        this.tableId = tableId;
        this.gameNode = gameNode;
        this.name = name;
        this.senderId = senderId;
        this.roomId = roomId;
        this.partner = partner;
        this.bet = bet;
        this.pot = pot;
        this.users = users;
        this.vip = vip;
    }

    public boolean isVip() {
        return vip;
    }

    public int getRoomId() {
        return roomId;
    }
}
