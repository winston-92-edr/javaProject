package com.mynet.shared.shutdown;

import com.mynet.shared.resource.CacheController;
import com.mynet.shared.user.ProxyUser;
import com.mynet.socialserver.SocialController;

import java.util.ArrayList;

public class SocialShutDownManager extends Thread{
    private int nodeId;
    private String groupId;

    public SocialShutDownManager(int nodeId, String groupId) {
        this.nodeId = nodeId;
        this.groupId = groupId;
    }

    @Override
    public void run() {
        System.out.println("Social server is shutting down! wait..");

        CacheController cacheController = CacheController.getInstance();
        cacheController.deleteSocialNode(nodeId, groupId);
        cacheController.publishSocialNodeDeleteEvent(nodeId);

//       for(ProxyUser user:SocialController.getInstance().getUsers().values()){
//           cacheController.setExpireToChatList(user.getId());
//       }
    }
}
