package com.mynet.shared.resource.db;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;

public class DatabaseWorker {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseWorker.class);

    private DatabaseQueueWorker[] workers;
    private BlockingQueue<DatabaseWork> queue;
    private static final int QUEUE_SIZE = 10000;

    private static DatabaseWorker INSTANCE;

    public static DatabaseWorker getInstance(){
        if(INSTANCE == null){
            INSTANCE = new DatabaseWorker();
        }
        return INSTANCE;
    }

    private DatabaseWorker()
    {
        init();
    }

    public void init(){
        queue = new ArrayBlockingQueue<>(QUEUE_SIZE);

        workers = new DatabaseQueueWorker[8];
        for (int i = 0; i < workers.length; i++) {
            workers[i] = new DatabaseQueueWorker(this.queue);
            workers[i].setName("DatabaseWorkerThread-" + i);
            workers[i].start();
        }
    }

    public void addWork(DatabaseWork work){
        queue.offer(work);
    }

    class DatabaseQueueWorker extends Thread{

        private BlockingQueue<DatabaseWork> queue;
        private boolean running = false;

        public DatabaseQueueWorker(BlockingQueue<DatabaseWork> queue){
            this.queue = queue;
            this.running = true;
        }

        @Override
        public void run() {
            while (running){
                try {
                    DatabaseWork work = queue.take();
                    Callable c = work.getCallable();
                    Object r = c.call();
                    if(work.getCallback() != null){
                        work.getCallback().callback(r);
                    }
                } catch (Exception e){
                    logger.error(e.getMessage(), e);
                }
            }
        }
    }
}
