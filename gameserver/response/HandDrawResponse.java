package com.mynet.gameserver.response;

import com.mynet.gameserver.model.TableUserShortModel;

import java.util.List;

public class HandDrawResponse {
    List<TableUserShortModel> users;
    int tableId;

    public HandDrawResponse(List<TableUserShortModel> users, int tableId) {
        this.users = users;
        this.tableId = tableId;
    }
}
