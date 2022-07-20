package com.mynet.gameserver.logs;

import com.mynet.gameserver.actions.GameAction;

import java.util.ArrayList;
import java.util.List;

public class GameRecord {
    private List<GameAction> actions;
    private transient GameSession gameSession;


    public GameRecord(GameSession gameSession) {
        this.gameSession = gameSession;
        actions = new ArrayList<GameAction>();
    }

    public void gameStarted() {

    }

    public void addAction(GameAction action) {
        this.actions.add(action);
    }
}
