package com.mynet.questservice;

public class ShutdownQuestService extends Thread {
    @Override
    public void run() {
        System.out.println("Service is shutting down! wait");
        QuestController.getInstance().controlConsumer(false);

        System.out.println("Service down is completed!");
    }
}