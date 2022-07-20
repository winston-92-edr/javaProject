package com.mynet.gameserver.response;

import com.mynet.gameserver.enums.GameStatus;
import com.mynet.gameserver.model.TableUserModel;
import com.mynet.gameserver.okey.Table;

import java.util.List;

public class JoinAnAudienceResponse {
    private long tableId;
    private List<TableUserModel> gamers;
    private List<TableUserModel> audiences;
    private GameStatus gameStatus;

    public long getTableId() {
        return tableId;
    }

    public GameStatus getGameStatus() {
        return gameStatus;
    }

    public int getIsPartner() {
        return isPartner;
    }

    public long getPotValue() {
        return potValue;
    }

    private int isPartner;
    private long potValue;

    public JoinAnAudienceResponse(Table table) {
        this.tableId = table.getTableId();
        this.gamers = table.getTableGamers();
        this.audiences = table.getTableAudiences();
        this.gameStatus = table.getGameStatus();
        this.isPartner = table.getIsPartner();
        this.potValue = table.getPotValue();
    }
}
