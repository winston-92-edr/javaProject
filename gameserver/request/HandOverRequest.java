package com.mynet.gameserver.request;

import com.mynet.gameserver.model.HandOverCardModel;
import com.mynet.gameserver.table.HandOverController;

import java.util.List;

public class HandOverRequest {
    private int side;
    private String cId;
    private boolean goDouble;
    private List<HandOverCardModel> hand;

    public int getSide() {
        return side;
    }

    public String getcId() {
        return cId;
    }

    public boolean isGoDouble() {
        return goDouble;
    }

    public List<HandOverCardModel> getHand() {
        return hand;
    }
}
