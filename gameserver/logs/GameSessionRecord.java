package com.mynet.gameserver.logs;

import com.mynet.gameserver.actions.GameAction;

import java.util.List;

public class GameSessionRecord {
    private transient GameSession gameSession;
    private long id;
    private List<GameRecord> games;

    public GameSessionRecord(GameSession gameSession) {
        this.gameSession = gameSession;
        this.id = this.gameSession.getId();
    }

    public void newGame(int currentGameNumber) {
        GameRecord gameRecord = new GameRecord(gameSession);
        games.add(gameRecord);
    }

    public void addGameAction(GameAction action){
        GameRecord lastGameRecord = getLastGameRecord();
        if(lastGameRecord != null)
            lastGameRecord.addAction(action);
    }

    public GameRecord getLastGameRecord(){
        if(games.isEmpty())
            return null;
        return games.get(games.size() - 1);
    }
}
