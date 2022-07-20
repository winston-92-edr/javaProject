package com.mynet.gameserver.room;

import com.mynet.shared.types.ServerType;

import java.beans.ConstructorProperties;

public class RoomType {
    private int id;
    private boolean vipMode;
    private int bet;
    private int minBet;
    private String name;
    private ServerType serverType;
    private int defaultTableCount;

    @ConstructorProperties({"ID", "TYPE1", "BET", "MIN_BET", "NAME", "TCOUNT"})
    public RoomType(int id, int vipMode, int bet, int minBet, String name, int defaultTableCount) {
        this.id = id;
        this.vipMode = vipMode == 1;
        this.bet = bet;
        this.minBet = minBet;
        this.name = name;
        this.defaultTableCount = defaultTableCount;
    }

    public RoomType(int id, boolean vipMode, int bet, int minBet, String name, ServerType serverType) {
        this.id = id;
        this.vipMode = vipMode;
        this.bet = bet;
        this.minBet = minBet;
        this.name = name;
        this.serverType = serverType;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public boolean getVipMode() {
        return vipMode;
    }

    public void setVipMode(boolean vipMode) {
        this.vipMode = vipMode;
    }

    public int getBet() {
        return bet;
    }

    public void setBet(int bet) {
        this.bet = bet;
    }

    public int getMinBet() {
        int val;
        switch (serverType) {
            case TOURNAMENT:
                val = 0;
                break;
            case DUELLO:
                val = bet;
                break;
            default:
                val = minBet;
        }
        return val;
    }

    public void setMinBet(int minBet) {
        this.minBet = minBet;
    }

    public void setServerType(ServerType serverType) {
        this.serverType = serverType;
    }

    public int getDefaultTableCount() {
        return defaultTableCount;
    }
}
