package com.mynet.gameserver.logs;

import com.mynet.gameserver.actions.GameAction;
import com.mynet.gameserver.okey.Table;

public class GameSession {
    private long id;
    private Table table;
    private int currentGameNumber;
    private long gameStartTime;
    private GameSessionRecord gameSessionRecord;

    public GameSession(Table table) {
        this.table = table;
    }

    public void startNewSession() {
        this.id = System.nanoTime();
        currentGameNumber = 0;
        gameSessionRecord = new GameSessionRecord(this);
    }

    public void nextGame() {
        currentGameNumber++;
        if(currentGameNumber == 1){
            gameStartTime = System.currentTimeMillis();
        }
        gameSessionRecord.newGame(currentGameNumber);
    }

    public void addGameAction(GameAction gameAction) {
        gameSessionRecord.addGameAction(gameAction);
    }

    public long getId() {
        return id;
    }
}
