package com.mynet.gameserver.builders;

import com.mynet.gameserver.model.InviteTableUserModel;
import com.mynet.gameserver.response.InviteResponse;

import java.util.List;

public class InviteResponseBuilder {
    private int tableId;
    private int gameNode;
    private String name;
    private String senderId;
    private int roomId;
    private boolean partner;
    private long bet;
    private long pot;
    private boolean vip;
    private List<InviteTableUserModel> users;

    public InviteResponseBuilder setTableId(int tableId) {
        this.tableId = tableId;
        return this;
    }

    public InviteResponseBuilder setGameNode(int gameNode) {
        this.gameNode = gameNode;
        return this;
    }

    public InviteResponseBuilder setName(String name) {
        this.name = name;
        return this;
    }

    public InviteResponseBuilder setSenderId(String senderId) {
        this.senderId = senderId;
        return this;
    }

    public InviteResponseBuilder setRoomId(int roomId) {
        this.roomId = roomId;
        return this;
    }

    public InviteResponseBuilder setPartner(boolean partner) {
        this.partner = partner;
        return this;
    }

    public InviteResponseBuilder setBet(long bet) {
        this.bet = bet;
        return this;
    }

    public InviteResponseBuilder setPot(long pot) {
        this.pot = pot;
        return this;
    }

    public InviteResponseBuilder setUsers(List<InviteTableUserModel> users) {
        this.users = users;
        return this;
    }

    public InviteResponseBuilder setVip(boolean vip) {
        this.vip = vip;
        return this;
    }

    public InviteResponse createInviteResponse() {
        return new InviteResponse(tableId, gameNode, name, senderId, roomId, partner, bet, pot, users, vip);
    }
}