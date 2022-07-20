package com.mynet.shared.analytics.model;

import com.mynet.shared.analytics.enums.EventType;

import java.util.Map;

public class GameLog extends BaseEvent{
    String gameId;
    String gameType; //classic,tournament
    boolean logType; //true,false(başladı, bitti)
    int gameResult; //0(kaybetti), 1(kazandı), 2(berabere), 3(oyun başladı)
    int tableType; //0(Tekli), 1(Eşli)

    public GameLog(String userId, String deviceId, String sessionId, EventType eventType, Map<String, Object> gameData, String gameId, String gameType, boolean logType, int gameResult, int tableType, String platform, String appVersion) {
        super(userId, deviceId, sessionId, eventType, gameData, platform, appVersion);
        this.gameId = gameId;
        this.gameType = gameType;
        this.logType = logType;
        this.gameResult = gameResult;
        this.tableType = tableType;
    }

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public String getGameType() {
        return gameType;
    }

    public void setGameType(String gameType) {
        this.gameType = gameType;
    }

    public boolean isLogType() {
        return logType;
    }

    public void setLogType(boolean logType) {
        this.logType = logType;
    }

    public int getGameResult() {
        return gameResult;
    }

    public void setGameResult(int gameResult) {
        this.gameResult = gameResult;
    }
}
