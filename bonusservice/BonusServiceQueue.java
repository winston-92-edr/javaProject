package com.mynet.bonusservice;
import com.mynet.shared.logs.BonusLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class BonusServiceQueue extends Thread{
    private BlockingQueue<BonusLog> queue;
    private static final int QUEUE_SIZE = 10000;
    private static final Logger logger = LoggerFactory.getLogger(BonusServiceQueue.class);
    private final BonusService bonusService;

    public BonusServiceQueue(BonusService bonusService) {
        queue = new ArrayBlockingQueue<>(QUEUE_SIZE);
        this.bonusService = bonusService;

        this.start();
    }

    public void add(BonusLog request){
        queue.offer(request);
    }

    @Override
    public void run() {
        logger.info("Bonus queue started.");
        while (true){
            try {
                BonusLog work = queue.take();
                bonusService.checkBonus(work);
            } catch (Exception e){
                logger.error(e.getMessage(), e);
            }
        }
    }
}
