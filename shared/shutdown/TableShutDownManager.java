package com.mynet.shared.shutdown;

import com.mynet.shared.resource.CacheController;

public class TableShutDownManager extends Thread{
    private int nodeId;
    private String groupId;

    public TableShutDownManager(int nodeId, String groupId) {
        this.nodeId = nodeId;
        this.groupId = groupId;
    }

    @Override
    public void run() {
        System.out.println("Table server is shutting down! wait..");

        CacheController cacheController = CacheController.getInstance();
        cacheController.deleteTableNode(groupId);
        cacheController.publishTableNodeDeleteEvent(nodeId);
    }
}
