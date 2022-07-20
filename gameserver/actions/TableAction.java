package com.mynet.gameserver.actions;

import com.mynet.gameserver.okey.Table;
import com.mynet.matchserver.GameUser;

public abstract class TableAction {
    protected GameUser user;
    protected Table table;
    protected long addingTime;

    public TableAction(Table table, GameUser user){
        this.table = table;
        this.user = user;
        this.addingTime = System.currentTimeMillis();
    }

    public GameUser getPlayer() {
        return user;
    }

    public void setPlayer(GameUser user) {
        this.user = user;
    }

    public abstract boolean process();

    public abstract GameAction getGameAction();

    public abstract String getName();

    public long getAddingTime(){
        return addingTime;
    }
}
