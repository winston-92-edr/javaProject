package com.mynet.gameserver.response;

import com.mynet.gameserver.model.TableUserModel;

public class JoinOpponentResponse {
    private TableUserModel user;

    public JoinOpponentResponse(TableUserModel user) {
        this.user = user;
    }
}
