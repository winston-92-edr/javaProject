package com.mynet.gameserver.table;

import com.mynet.gameserver.enums.GameStatus;
import com.mynet.gameserver.okey.Table;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

public class TableController {
    private static final Logger logger = LoggerFactory.getLogger(TableController.class);
    public final ConcurrentHashMap<Integer, Table> tableMap;

    public TableController() {
        tableMap = new ConcurrentHashMap<>();
    }

    public List<Table> getTables(){
        return new ArrayList<>(tableMap.values());
    }

    public void addTable(Table table)
    {
        tableMap.put(table.getTableId(), table);
    }

    public void removeTable(Table table)
    {
        tableMap.remove(table.getTableId());
    }

    public Table getTable(int tableId){
        return tableMap.get(tableId);
    }

    public int getSize()
    {
        return tableMap.size();
    }

    public TableCount getPlayingTablesCount() {

        int playing = 0;
        int waiting = 0;


        for (Table t: tableMap.values()){
            if (t.getGameStatus() == GameStatus.PLAYING) {
                playing++;
            } else {
                waiting++;
            }
        }

        return new TableCount(playing, waiting);

    }
}
