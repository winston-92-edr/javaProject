package com.mynet.shared.user;

import com.mynet.gameserver.enums.EventDbLogType;
import com.mynet.gameserver.logs.EventDbLogData;
import com.mynet.gameserver.okey.Table;
import com.mynet.shared.logs.TournamentEventLog;
import com.mynet.shared.model.BasicUserModel;

public interface IPlayer {
    String getfuid();
    public void changeEventType(EventDbLogType type);

    public void beginTournamentEvent(TournamentEventLog.Type type);

    public void fillTournamentEvent(int tournamentId, boolean success, long gameId, int cost, long amount);
    public void endTournamentEvent();

    public void setEvent(EventDbLogType type);

    public EventDbLogData getEvent();

    public void resetEvent(boolean status, Table table);

    public BasicUserModel getBasicUserResponse();

    public void setHasMoneyInPot(boolean value);

    public boolean getIsVip();

    public long getMoney();

    public boolean isFakeUser();
}
