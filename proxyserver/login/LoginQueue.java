package com.mynet.proxyserver.login;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.mynet.shared.node.NodeController;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class LoginQueue extends Thread {

    private static final Logger logger = LoggerFactory.getLogger(LoginQueue.class);
    private BlockingQueue<LoginTask> queue;
    private static final int QUEUE_SIZE = 1000;
    private volatile boolean isRunning ;
    private AuthorizeQueue authorizeQueue;

    private long lastVarUpdate;
    private static LoginQueue INSTANCE;

    private LoginQueue(AuthorizeQueue authorizeQueue){
        this.authorizeQueue = authorizeQueue;
        queue = new ArrayBlockingQueue<>(QUEUE_SIZE);
        isRunning = true;
    }

    public static void init(NodeController nodeController){

        if(INSTANCE == null){
            AuthorizeQueue authorizeQueue = new AuthorizeQueue(nodeController);
            authorizeQueue.start();
            LoginQueue loginQueue = new LoginQueue(authorizeQueue);
            INSTANCE = loginQueue;
            loginQueue.start();
        }
    }

    public static LoginQueue getInstance(){
        return INSTANCE;
    }


    public boolean add(LoginTask task){
        return this.queue.offer(task);
    }

    public void processTask(LoginTask task) {
        AuthorizeTask authorizeTask = new AuthorizeTask(task.getRequest(), task.getChannel(), task.getHandler(),  task.getRequest().getId(), task.getRequest().getPlatform());
        authorizeQueue.add(authorizeTask);
    }

    @Override
    public void run() {

        while (isRunning){
            try {
                LoginTask task = queue.take();


                if((System.currentTimeMillis() - lastVarUpdate) > 10000) {
                    lastVarUpdate = System.currentTimeMillis();
                }

                processTask(task);

            } catch (InterruptedException e) {
                e.printStackTrace();
                Thread.currentThread().interrupt();
            }catch (Exception ex){
                logger.error(ex.getMessage(), ex);
            }
        }


    }

}
