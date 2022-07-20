package com.mynet.questservice.consumer;

import com.mynet.questservice.QuestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class QuestEventQueue{
    private static final Logger logger = LoggerFactory.getLogger(QuestEventQueue.class);

    private final QuestController questController;
    private BlockingQueue<String> queue;
    private final static int QUEUE_SIZE = 5000;
    private final static int THREAD_COUNT = 8;
    private boolean running;

    public QuestEventQueue(QuestController controller){
        questController = controller;
        queue = new ArrayBlockingQueue<>(QUEUE_SIZE);

        for (int i = 0; i < THREAD_COUNT; i++) {
            new Consumer(queue, "QuestEventConsumer-"+i);
        }
        running = true;
    }

    public void add(String message){
        queue.offer(message);
    }

    class Consumer extends Thread{
        private final BlockingQueue<String> queue;

        public Consumer(BlockingQueue<String> queue, String name) {
            this.queue = queue;
            this.setName(name);
           start();
        }

        @Override
        public void run() {
            while (running){
                try {
                    String message = queue.take();
                    questController.check(message);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    logger.error(e.getMessage(), e);
                }
            }
        }
    }
}
