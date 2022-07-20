package com.mynet.tableservice.actions;

import com.mynet.tableservice.service.TableService;

public class RemoveTableAction extends AbstractServiceAction{
    private int tableId;
    private int roomId;

    public RemoveTableAction(int tableId, int roomId) {
        this.tableId = tableId;
        this.roomId = roomId;
    }

    @Override
    public void process() {
        TableService.getInstance().removeTable(tableId, roomId);
    }
}
