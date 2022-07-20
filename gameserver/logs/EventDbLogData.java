package com.mynet.gameserver.logs;

import com.mynet.gameserver.enums.EventDbLogType;
import com.mynet.gameserver.okey.GameSide;
import com.mynet.gameserver.okey.Table;
import com.mynet.matchserver.GameUser;
import com.mynet.shared.logs.QueueElement;
import com.mynet.shared.types.ServerType;

public class EventDbLogData extends QueueElement {
    public static final int BUTTON_UNKNOWN = 0;
    public static final int MAIN_QUICKPLAY = 1;
    public static final int ROOM_QUICKPLAY = 2;

    private long id;
    private long start;
    private long end;
    private EventDbLogType type;
    private long gameID = -1;
    private long tableID = -1;
    private boolean success;
    private int button;
    private long money;
    private long bet;
    private int side = -1;
    private int roomID = -1;
    private boolean sitSuccess;
    private String side0;
    private String side1;
    private String side2;
    private String side3;
    private String userID;
    private long pot;
    private int paired;

    private transient GameUser user;

    private String game_mode;

    public EventDbLogData(GameUser user, EventDbLogType type, int button, ServerType serverType) {
        this.id = System.nanoTime();
        this.button = button;
        this.user = user;
        this.userID = user.getId();
        game_mode = serverType.toString();
        setType(type);
        this.start = System.currentTimeMillis();
    }

    public void fillTableData(Table table){

        setMoney(user.getBasicUser().getMoney());
        if (table == null) {
            setSitSuccess(false);
            return;
        }
        GameSide p0 = table.getGameSide(0);
        GameSide p1 = table.getGameSide(1);
        GameSide p2 = table.getGameSide(2);
        GameSide p3 = table.getGameSide(3);

        setSide0(p0 != null ? p0.getFuid() : null);
        setSide1(p1 != null ? p1.getFuid() : null);
        setSide2(p2 != null ? p2.getFuid() : null);
        setSide3(p3 != null ? p3.getFuid() : null);
        setPot(table.getPotValue());
        setPaired(table.getIsPartner());



        setBet(table.getBet());
        setRoomID(table.getRoomId());
        setTableID(table.getTableId());
        setPaired(table.getIsPartner());
        setPot(table.getPotValue());

        setGameID(table.getGameId());

        setSide(table.getGamerSide(user.getId()));

        if(getSide() != -1) {
            setSitSuccess(true);
        }
    }

    public void setPot(long pot) {
        this.pot = pot;
    }

    public void setPaired(int paired) {
        this.paired = paired;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getStart() {
        return start;
    }

    public void setStart(long start) {
        this.start = start;
    }

    public long getEnd() {
        return end;
    }

    public void setEnd(long end) {
        this.end = end;
    }

    public EventDbLogType getType() {
        return type;
    }

    public void setType(EventDbLogType type) {
        this.type = type;
    }

    public long getGameID() {
        return gameID;
    }

    public void setGameID(long gameID) {
        this.gameID = gameID;
    }

    public long getTableID() {
        return tableID;
    }

    public void setTableID(int tableID) {
        this.tableID = tableID;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public int getButton() {
        return button;
    }

    public void setButton(int button) {
        this.button = button;
    }

    public long getMoney() {
        return money;
    }

    public void setMoney(long money) {
        this.money = money;
    }


    public long getBet() {
        return bet;
    }

    public void setBet(long bet) {
        this.bet = bet;
    }

    public int getSide() {
        return side;
    }

    public void setSide(int side) {
        this.side = side;
    }

    public int getRoomID() {
        return roomID;
    }

    public void setRoomID(int roomID) {
        this.roomID = roomID;
    }

    public boolean isSitSuccess() {
        return sitSuccess;
    }

    public void setSitSuccess(boolean sitSuccess) {
        this.sitSuccess = sitSuccess;
    }

    public String getSide0() {
        return side0;
    }

    public void setSide0(String side0) {
        this.side0 = side0;
    }

    public String getSide1() {
        return side1;
    }

    public void setSide1(String side1) {
        this.side1 = side1;
    }

    public String getSide2() {
        return side2;
    }

    public void setSide2(String side2) {
        this.side2 = side2;
    }

    public String getSide3() {
        return side3;
    }

    public void setSide3(String side3) {
        this.side3 = side3;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }


    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();
        sb.append(getBet());
        sb.append(";");
        sb.append(getEnd());
        sb.append(";");
        sb.append(getGameID());
        sb.append(";");
        sb.append(getId());
        sb.append(";");
        sb.append(getMoney());
        sb.append(";");
        sb.append(getType());
        sb.append(";");
        sb.append(getRoomID());
        sb.append(";");
        sb.append(getSide());
        sb.append(";");
        sb.append(getSide0());
        sb.append(";");
        sb.append(getSide1());
        sb.append(";");
        sb.append(getSide2());
        sb.append(";");
        sb.append(getSide3());
        sb.append(";");
        sb.append(isSitSuccess());
        sb.append(";");
        sb.append(getStart());
        sb.append(";");
        sb.append(isSuccess());
        sb.append(";");
        sb.append(getTableID());
        sb.append(";");
        sb.append(getUserID());
        sb.append(";");
        sb.append(game_mode);

        String parameters = sb.toString();

        return parameters;
    }
}
