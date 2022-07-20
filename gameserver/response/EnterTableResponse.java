package com.mynet.gameserver.response;

import com.mynet.gameserver.enums.GameStatus;
import com.mynet.gameserver.model.TableUserModel;
import com.mynet.gameserver.okey.Table;

import java.util.List;

public class EnterTableResponse {
    private long tableId;
    private List<TableUserModel> gamers;
    private List<TableUserModel> audiences;
    private GameStatus gameStatus;
    private int isPartner;
    private long potValue;
    private boolean audience;
    private int side;
    private int roomId;
    private String roomName;
    private boolean freePlay;

    public EnterTableResponse(Table table, Boolean audience, int side, String roomName, boolean freePlay) {
        this.tableId = table.getTableId();
        this.gamers = table.getTableGamers();
        this.audiences = table.getTableAudiences();
        this.gameStatus = table.getGameStatus();
        this.isPartner = table.getIsPartner();
        this.potValue = table.getPotValue();
        this.audience = audience;
        this.side = side;
        this.roomId = table.getRoomId();
        this.roomName = roomName;
        this.freePlay = freePlay;
    }
}
