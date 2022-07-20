package com.mynet.gameserver.response;

import com.mynet.gameserver.enums.RemoveUserMessages;

public class LeftTableResponse {
    RemoveUserMessages message;

    public LeftTableResponse(RemoveUserMessages message) {
        this.message = message;
    }
}
