package com.mynet.gameserver.actions;

import com.mynet.gameserver.enums.GameRecordType;
import com.mynet.gameserver.logs.BaseGameActionRecord;
import com.mynet.shared.network.GameCommands;

public class GameAction {
    private long timestamp;
    private GameCommands type;
    private BaseGameActionRecord data;
    private boolean isSuccess;

    public GameAction( GameCommands type, BaseGameActionRecord data) {
        this.type = type;
        this.data = data;
        this.timestamp = System.currentTimeMillis();
    }

    public GameAction(GameCommands type) {
        this.type = type;
        this.timestamp = System.currentTimeMillis();
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean success) {
        isSuccess = success;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public GameCommands getType() {
        return type;
    }

    public void setType(GameCommands type) {
        this.type = type;
    }

    public BaseGameActionRecord getData() {
        return data;
    }

    public void setData(BaseGameActionRecord data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "GameAction{" +
                "timestamp=" + timestamp +
                ", type=" + type +
                ", data=" + data +
                '}';
    }
}
