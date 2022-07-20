package com.mynet.gameserver.table;

import com.mynet.gameserver.okey.Table;
import com.mynet.gameserver.GameController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

public class TableWatcherController {
    private static final Logger logger = LoggerFactory.getLogger(TableWatcherController.class);
    private static TableWatcherController INSTANCE;
    public static final int THREAD_COUNT = 16;
    private static final int LOOP_TIME = 50; // ms

    private Thread threads[];
    private volatile  boolean isRunning = true;
    private CopyOnWriteArrayList<Table>[] tableArrays;
    private ArrayList<Table>[] removingTableArrays;

    private int currentThreadMark;

    public static TableWatcherController getInstance()
    {
        if(INSTANCE == null)
        {
            INSTANCE = new TableWatcherController();
        }

        return INSTANCE;
    }

    private TableWatcherController() { }

    public void init() {
        tableArrays = new CopyOnWriteArrayList[THREAD_COUNT];
        removingTableArrays = new ArrayList[THREAD_COUNT];
        threads = new Thread[THREAD_COUNT];

        for (int i = 0; i < THREAD_COUNT; i++) {

            CopyOnWriteArrayList<Table> tableArray = new CopyOnWriteArrayList<>();
            ArrayList<Table> removingTableList = new ArrayList<>();

            removingTableArrays[i] = removingTableList;
            tableArrays[i] = tableArray;
            threads[i] = new Thread(new TableWatcherRunnable(tableArray, removingTableList));
            threads[i].start();
        }
    }


    public synchronized void addTable(Table table)
    {
        int mark = (++currentThreadMark) % THREAD_COUNT;
        table.setTableThreadMark(mark);
        tableArrays[mark].add(table);
        currentThreadMark = mark;
    }

    public synchronized void removeTable(Table table)
    {
        int mark = table.getTableThreadMark();
        removingTableArrays[mark].add(table);
    }

    public synchronized void deleteTable(Table table)
    {
        int mark = table.getTableThreadMark();
        tableArrays[mark].remove(table);
    }

    public synchronized int getTableArrayCount(){
        return tableArrays.length;
    }

    public synchronized int getRemoveTableArrayCount(){
        return removingTableArrays.length;
    }

    class TableWatcherRunnable implements Runnable {

        private CopyOnWriteArrayList<Table> tables;
        private ArrayList<Table> removingTables;

        public TableWatcherRunnable(CopyOnWriteArrayList<Table> tables, ArrayList<Table> removingTables){
            this.tables = tables;
            this.removingTables = removingTables;
        }

        public void run() {
            while (isRunning) {
                long start = System.currentTimeMillis();
                for (Table table : tables) {
                    table.tableLoop.process(LOOP_TIME);
                }
                long processEnd = System.currentTimeMillis();

                for (Table table : removingTables) {
                    GameController.getInstance().removeTable(table);
                }
                removingTables.clear();

                long end = System.currentTimeMillis();
                long time = Math.max(0, (LOOP_TIME - (end - start)));

                try {
                    if(time > 3000){
                        long processTime = processEnd - start;
                        logger.warn(String.format("[SLOW ACTION ON MAIN PROCESS][time: %d][processTime: %d]", time, processTime));
                    }

                    Thread.sleep(time);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
