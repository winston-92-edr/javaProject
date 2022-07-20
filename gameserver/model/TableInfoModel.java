package com.mynet.gameserver.model;

import com.mynet.tableservice.enums.TableFullnessFilter;
import com.mynet.tableservice.enums.TablePairedFilter;
import com.mynet.tableservice.enums.TableRobotFilter;

import java.util.List;

public class TableInfoModel {
    long tableId;
    long potValue;
    int roomId;
    boolean paired;
    int gameServerId;
    List<TableUserModel> gamers;
    long bet;

    public TableInfoModel(long tableId, long potValue, int roomId, boolean paired, int gameServerId, List<TableUserModel> gamers, long bet) {
        this.tableId = tableId;
        this.potValue = potValue;
        this.roomId = roomId;
        this.paired = paired;
        this.gameServerId = gameServerId;
        this.gamers = gamers;
        this.bet = bet;
    }

    public long getTableId() {
        return tableId;
    }

    public long getPotValue() {
        return potValue;
    }

    public int getRoomId() {
        return roomId;
    }

    public boolean isPaired() {
        return paired;
    }

    public int getGameServerId() {
        return gameServerId;
    }

    public List<TableUserModel> getGamers() {
        return gamers;
    }

    public boolean isBotTable(){
        return gamers.stream().filter(x -> x.bot).count() > 0;
    }

    public long getBet() {
        return bet;
    }

    public void setTableId(long tableId) {
        this.tableId = tableId;
    }

    public boolean filterTable(TableFilterModel filter){
        TablePairedFilter pairedFilter = filter.getPaired();
        TableRobotFilter robotFilter = filter.getRobot();
        TableFullnessFilter fullnessFilter = filter.getFullness();

        return (pairedFilter.equals(TablePairedFilter.ALL) || (isPaired() &&
                pairedFilter.equals(TablePairedFilter.PAIRED)) || (!isPaired() &&
                pairedFilter.equals(TablePairedFilter.NOT_PAIRED))) &&
                (robotFilter.equals(TableRobotFilter.ALL) || (isBotTable() &&
                        robotFilter.equals(TableRobotFilter.ROBOT)) || (!isBotTable() &&
                        robotFilter.equals(TableRobotFilter.NOT_ROBOT))) &&
                ((fullnessFilter.equals(TableFullnessFilter.FULL) && gamers.size() == 4) ||
                        (fullnessFilter.equals(TableFullnessFilter.HALF_FULL) && (gamers.size() < 4 && gamers.size() > 0)) ||
                        (fullnessFilter.equals(TableFullnessFilter.EMPTY) && gamers.size() == 0) || fullnessFilter.equals(TableFullnessFilter.ALL));
    }
}
