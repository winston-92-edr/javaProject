package com.mynet.shared.shutdown;

import com.mynet.shared.resource.CacheController;

public class ChatShutDownManager extends Thread{
    private int nodeId;
    private String groupId;

    public ChatShutDownManager(int nodeId, String groupId) {
        this.nodeId = nodeId;
        this.groupId = groupId;
    }

    @Override
    public void run() {
        System.out.println("Chat server is shutting down! wait..");

        CacheController cacheController = CacheController.getInstance();
        cacheController.deleteChatNode(groupId);
        cacheController.publishChatNodeDeleteEvent(nodeId);
    }
}
