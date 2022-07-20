package com.mynet.shared.shutdown;

import com.mynet.bonusservice.BonusService;
import com.mynet.shared.resource.CacheController;

public class BonusShutDownManager extends Thread{
    private int nodeId;
    private String groupId;

    public BonusShutDownManager(int nodeId, String groupId) {
        this.nodeId = nodeId;
        this.groupId = groupId;
    }

    @Override
    public void run() {
        System.out.println("Bonus server is shutting down! wait..");

        CacheController cacheController = CacheController.getInstance();
        cacheController.deleteBonusNode(groupId);
        cacheController.publishBonusNodeDeleteEvent(nodeId);

        BonusService.getInstance().controlConsumer(false);
    }
}
