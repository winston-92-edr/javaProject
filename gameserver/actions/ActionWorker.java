package com.mynet.gameserver.actions;

import com.mynet.gameserver.okey.Table;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class ActionWorker {
    private static final Logger logger = LoggerFactory.getLogger(ActionWorker.class);

    private BlockingQueue<TableAction> gameActions;
    private BlockingQueue<TableAction> tableActions;

    public ActionWorker() {
        gameActions = new ArrayBlockingQueue<>(200);
        tableActions = new ArrayBlockingQueue<>(200);
    }

    public void addGameAction(TableAction action) {
        gameActions.offer(action);
    }

    public void addTableAction(TableAction action) {
        tableActions.offer(action);
    }


    public void process() {
        processGameActions();
        processTableActions();
    }

    private void processTableActions() {
        TableAction action = tableActions.poll();

        while (action != null) {
            try {
                long diff = System.currentTimeMillis() - action.getAddingTime();
                if(diff > 3000){
                    logger.warn(String.format("[SLOW ACTION PROCESS: TABLE] [ACTION: %s] [diff: %d]", action.getName(), diff));
                }

                boolean isSuccess = action.process();

                // for game recording
                //TODO addGameAction(isSuccess, action);

            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
            action = tableActions.poll();
        }

    }

    private void processGameActions() {
        TableAction action = gameActions.poll();

        while (action != null) {
            try {
                long diff = System.currentTimeMillis() - action.getAddingTime();
                if(diff > 3000){
                    logger.warn(String.format("[SLOW ACTION PROCESS: GAME] [ACTION: %s] [diff: %d]", action.getName(), diff));
                }

                boolean isSuccess = action.process();

                // for game recording
                //TODO addGameAction(isSuccess, action);

            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
            action = gameActions.poll();
        }
    }
}
