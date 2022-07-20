package com.mynet.gameserver.model;

import java.util.Arrays;

public class VisitedTable {
    private final Integer[] tables;
    private int index;

    public VisitedTable() {
        this.index = 0;
        this.tables = new Integer[2];
    }

    public void storeTable(int tableId){
        if(isStored(tableId)) return;

        if(index == 2){
            index = 0;
        }

        tables[index] = tableId;
        index++;
    }

    public boolean isStored(int tableId){
        return Arrays.asList(tables).contains(tableId);
    }
}
