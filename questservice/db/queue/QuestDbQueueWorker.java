package com.mynet.questservice.db.queue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.BlockingQueue;

class QuestDbQueueWorker extends Thread {
    private static final Logger logger = LoggerFactory.getLogger(QuestDbQueueWorker.class);
    private BlockingQueue<QuestQuery> queue;
    private boolean isRunning;

    public QuestDbQueueWorker(BlockingQueue queue) {
        this.queue = queue;
        this.isRunning = true;
    }

    @Override
    public void run() {

        while (isRunning) {
            try{
                QuestQuery preparedStatement = queue.take();
                preparedStatement.execute();

            } catch (Exception e) {
                logger.info(e.getMessage());
            }
        }

    }
}