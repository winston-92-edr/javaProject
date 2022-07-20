package com.mynet.gameserver.response;

import com.mynet.gameserver.model.TableUserShortModel;

import java.util.List;

public class GetUsersInLobbyResponse {
    List<TableUserShortModel> users;

    public GetUsersInLobbyResponse(List<TableUserShortModel> users) {
        this.users = users;
    }
}
