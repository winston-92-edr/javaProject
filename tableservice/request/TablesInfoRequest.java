package com.mynet.tableservice.request;

import com.mynet.tableservice.enums.TableFullnessFilter;
import com.mynet.tableservice.enums.TablePairedFilter;
import com.mynet.tableservice.enums.TableRobotFilter;

public class TablesInfoRequest {
    public final int roomId;
    public TablePairedFilter paired;
    public TableRobotFilter robot;
    public TableFullnessFilter fullness;

    public TablesInfoRequest(int roomId, TablePairedFilter paired, TableRobotFilter robot, TableFullnessFilter fullness) {
        this.roomId = roomId;
        this.paired = paired;
        this.robot = robot;
        this.fullness = fullness;
    }
}