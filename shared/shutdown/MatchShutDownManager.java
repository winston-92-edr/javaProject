package com.mynet.shared.shutdown;

import com.mynet.shared.resource.CacheController;

public class MatchShutDownManager extends Thread{
    private int nodeId;
    private String groupId;

    public MatchShutDownManager(int nodeId, String groupId) {
        this.nodeId = nodeId;
        this.groupId = groupId;
    }

    @Override
    public void run() {
        System.out.println("Match server is shutting down! wait..");

        CacheController cacheController = CacheController.getInstance();
        cacheController.deleteMatchNode(groupId);
        cacheController.publishMatchNodeDeleteEvent(nodeId);
    }
}
