package com.mynet.tableservice.service;

import com.mynet.tableservice.actions.AbstractServiceAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class TableServiceWorker {
    private static final Logger logger = LoggerFactory.getLogger(TableServiceWorker.class);

    private BlockingQueue<AbstractServiceAction> actions;
    private Thread workerThread;
    private boolean running;

    public TableServiceWorker() {
        actions = new ArrayBlockingQueue<>(200);
        workerThread = new Thread(()->{
            running = true;
            try {
                while (running){
                    process();
                    Thread.sleep(50);
                }

            }catch (Exception e){
                running = false;
            }
        });
        workerThread.start();
    }

    public void add(AbstractServiceAction action){
        actions.offer(action);
    }

    public void process(){
        while (actions.size() > 0){
            AbstractServiceAction action = actions.poll();
            action.process();
        }
    }


}
