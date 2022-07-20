package com.mynet.gameserver.model;

import com.mynet.tableservice.enums.TableFullnessFilter;
import com.mynet.tableservice.enums.TablePairedFilter;
import com.mynet.tableservice.enums.TableRobotFilter;

public class TableFilterModel {
    private int roomId;
    private TablePairedFilter paired;
    private TableRobotFilter robot;
    private TableFullnessFilter fullness;

    public TableFilterModel() {
        this.roomId = -1;
        this.paired = TablePairedFilter.ALL;
        this.robot = TableRobotFilter.ALL;
        this.fullness = TableFullnessFilter.ALL;
    }

    public TableFilterModel(TablePairedFilter paired, TableRobotFilter robot, TableFullnessFilter fullness) {
        this.paired = paired;
        this.robot = robot;
        this.fullness = fullness;
    }

    public int getRoomId() {
        return roomId;
    }

    public TablePairedFilter getPaired() {
        return paired;
    }

    public TableRobotFilter getRobot() {
        return robot;
    }

    public TableFullnessFilter getFullness(){ return fullness; }

}
