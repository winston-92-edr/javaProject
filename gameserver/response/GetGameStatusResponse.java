package com.mynet.gameserver.response;

import com.mynet.gameserver.enums.GameStatus;

public class GetGameStatusResponse {
    GameStatus gameStatus;

    public GetGameStatusResponse(GameStatus gameStatus) {
        this.gameStatus = gameStatus;
    }
}
