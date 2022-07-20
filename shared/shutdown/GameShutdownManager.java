package com.mynet.shared.shutdown;

import com.mynet.gameserver.GameController;
import com.mynet.gameserver.okey.Table;
import com.mynet.gameserver.room.RoomType;
import com.mynet.matchserver.GameUser;
import com.mynet.shared.resource.CacheController;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class GameShutdownManager extends Thread {

    private int nodeId;
    private String groupId;

    public GameShutdownManager(int nodeId, String groupId) {
        this.nodeId = nodeId;
        this.groupId = groupId;
    }

    @Override
    public void run() {
        System.out.println("Game server is shutting down! wait..");

        CacheController cacheController = CacheController.getInstance();
        GameController gameController = GameController.getInstance();

        List<Table> tables = gameController.getTables();

        cacheController.deleteAvailableGameNode(nodeId, groupId);
        cacheController.publishGameNodeDeleteEvent(nodeId);

        for (Table table : tables){
            gameController.removeFromTableService(table.getTableId()+ "", table.getRoomId());
//            if(table.getUserCount() > 0){
//                table.saveGame();
//            }
            
            table.kickAllAudiences();
            Collection<GameUser> users = table.getUsers().values();
            for (GameUser user: users) {
                cacheController.resetUserGameAndTable(user.getId());
            }
        }

        cacheController.clearNodeUserCount(nodeId);

        ArrayList<RoomType> roomTypeList = gameController.getRoomTypeList();
        for (RoomType room: roomTypeList){
            cacheController.clearNodeRoomUsersCount(nodeId, room.getId());
        }

    }
}
