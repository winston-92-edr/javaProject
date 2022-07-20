package com.mynet.shared.shutdown;

import com.mynet.questservice.QuestController;
import com.mynet.shared.resource.CacheController;

public class QuestShutDownManager extends Thread {
    private int nodeId;
    private String groupId;

    public QuestShutDownManager(int nodeId, String groupId) {
        this.nodeId = nodeId;
        this.groupId = groupId;
    }

    @Override
    public void run() {
        System.out.println("Service is shutting down! wait");

        CacheController cacheController = CacheController.getInstance();
        cacheController.deleteQuestlNode(groupId);
        cacheController.publishQuestNodeDeleteEvent(nodeId);

        QuestController.getInstance().controlConsumer(false);

        System.out.println("Service down is completed!");
    }
}
