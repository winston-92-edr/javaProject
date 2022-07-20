package com.mynet.matchserver.model;

import com.mynet.shared.types.GameType;

public class GameTypeInfo {
    private final GameType gameType;
    private final int playerCount;

    public GameTypeInfo(GameType gameType, int playerCount) {
        this.gameType = gameType;
        this.playerCount = playerCount;
    }

    public GameType getGameType() {
        return gameType;
    }

    public int getPlayerCount() {
        return playerCount;
    }
}
