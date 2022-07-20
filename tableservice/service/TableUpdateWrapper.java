package com.mynet.tableservice.service;

import com.mynet.gameserver.enums.TableUpdateType;
import com.mynet.shared.model.BasicUserModel;

public class TableUpdateWrapper {
    private int tableId;
    private TableUpdateType type;
    private BasicUserModel user;
    private int side;
    private long pot;

    public TableUpdateWrapper(int tableId, TableUpdateType type, int side, BasicUserModel user) {
        this.type = type;
        this.user = user;
        this.side = side;
        this.tableId = tableId;
        this.pot = 0;
    }

    public TableUpdateType getType() {
        return type;
    }

    public BasicUserModel getUser() {
        return user;
    }

    public int getSide() {
        return side;
    }

    public int getTableId() {
        return tableId;
    }

    public long getPot() {
        return pot;
    }

    public void setPot(long pot) {
        this.pot = pot;
    }
}
