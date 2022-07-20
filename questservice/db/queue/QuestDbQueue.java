package com.mynet.questservice.db.queue;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class QuestDbQueue {
    private static BlockingQueue<QuestQuery> statements;
    private static int queueSize = 1000;
    private QuestDbQueueWorker worker;

    public QuestDbQueue() {
        statements = new ArrayBlockingQueue<QuestQuery>(queueSize);
        worker = new QuestDbQueueWorker(statements);
        worker.start();
    }

    public void addStatement(QuestQuery query){
        statements.offer(query);
    }
}


