package com.mynet.shared.shutdown;

import com.mynet.shared.network.GameCommands;
import com.mynet.shared.network.NetworkMessage;
import com.mynet.shared.resource.CacheController;
import com.mynet.shared.resource.db.DBController;
import com.mynet.shared.user.ProxyUser;
import com.mynet.shared.user.UserController;

import java.util.Iterator;

public class ProxyShutdownManager extends Thread {

    private final int nodeId;

    public ProxyShutdownManager(int nodeId) {
        this.nodeId = nodeId;
    }

    @Override
    public void run() {
        System.out.println("Proxy is shutting down! wait..");

        UserController userController = UserController.getInstance();
        Iterator<ProxyUser> usersIterator = userController.getUserIterator();

        while(usersIterator.hasNext()){
            ProxyUser user = usersIterator.next();
            user.send(new NetworkMessage(GameCommands.SOCKET_DISCONNECT));
            userController.makeUserOfflineAtCache(user);
            userController.clearChatHistory(user);
        }

        CacheController.getInstance().flushOnlineUsers();

        DBController.getInstance().resetLobbyCounts(nodeId);
    }
}
