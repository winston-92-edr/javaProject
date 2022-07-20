package com.mynet.questservice;

public class HealthCheckThread implements Runnable {
    boolean isRunning = false;

    public void start() {
        this.isRunning = true;
        Thread worker = new Thread(this);
        worker.start();
    }

    @Override
    public void run() {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        while (isRunning) {
            boolean connected = QuestController.getInstance().isProxyConnected();
            if (!connected) QuestController.getInstance().controlConsumer(false);

            try {
                Thread.sleep(30000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }
}
