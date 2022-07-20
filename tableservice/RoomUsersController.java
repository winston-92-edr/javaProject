package com.mynet.tableservice;

import com.google.gson.Gson;
import com.mynet.shared.model.RoomCountModel;
import com.mynet.shared.node.NodeData;
import com.mynet.shared.resource.CacheController;
import com.mynet.shared.resource.db.DBController;
import com.mynet.shared.types.ServerType;
import com.mynet.socialserver.model.RoomUserCountModel;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RoomUsersController {
    private ArrayList<RoomUserCountModel> userCounts;
    private final Gson gson;
    private List<NodeData> gameNodes;
    private final String groupId;

    private static Logger logger = LoggerFactory.getLogger(RoomUsersController.class);

    public RoomUsersController(String groupId) {
        this.gson = new Gson();
        this.groupId = groupId;
        gameNodes = CacheController.getInstance().getGameNodes(groupId);
        addRedisListeners();


        Timer timer = new Timer();
        TimerTask timerTask = new RefreshUserCountsTask();
        timer.scheduleAtFixedRate(timerTask, 5000, 120000);
    }

    public ArrayList<RoomUserCountModel> getUserCounts(){
        return userCounts;
    }

    private RoomCountModel getNodeCounts(NodeData gameNode){

        try {
            String getUrl = "http://" + gameNode.getHost() + ":" + gameNode.getHttpPort() + "/table/counts";
            CloseableHttpClient httpClient = HttpClientBuilder.create().build();
            HttpGet get = new HttpGet(getUrl);
            HttpResponse response = httpClient.execute(get);
            httpClient.close();
            HttpEntity entity = response.getEntity();
            if(entity == null) return null;

            String body = EntityUtils.toString(entity);
            return gson.fromJson(body, RoomCountModel.class);
        } catch (IOException e) {
            logger.error(e.getMessage(), e.getCause());
        }
        return null;
    }

    private void addRedisListeners(){
        CacheController.getInstance().listenGameNodeAddEvent((charSequence, nodeData) -> {
            if(nodeData.getGroupId().equals(groupId) && nodeData.getServerType().equals(ServerType.GENERIC)){
                gameNodes.add(nodeData);
            }
        });

        CacheController.getInstance().listenGameNodeDeleteEvent((charSequence, nodeId) -> {
            Stream<NodeData> nodeDataStream = gameNodes.stream().filter(nodeData -> nodeData.getId() != nodeId);
            gameNodes = nodeDataStream.collect(Collectors.toList());
        });
    }

    public List<NodeData> getGameNodes(){
        return gameNodes;
    }

    class RefreshUserCountsTask extends TimerTask {

        @Override
        public void run() {
            Integer ccu = DBController.getInstance().getCCU();
            StringBuilder info = new StringBuilder();
            HashMap<Integer, Integer> totalCounts = new HashMap<>();
            for (NodeData nodeData : gameNodes){
                RoomCountModel counts = getNodeCounts(nodeData);

                if(counts == null) continue;
                for (Integer roomId: counts.getCounts().keySet()){
                    if(totalCounts.containsKey(roomId)){
                        Integer count = totalCounts.get(roomId);
                        totalCounts.replace(roomId, count + counts.getCounts().get(roomId));
                    }else{
                        totalCounts.put(roomId, counts.getCounts().get(roomId));
                    }
                }
            }

            Integer gameNodeUsersCount = 0;
            for (Integer roomId : totalCounts.keySet()) {
                gameNodeUsersCount += totalCounts.get(roomId);
            }
            if(ccu == null){
                ccu = gameNodeUsersCount;
            }

            ArrayList<RoomUserCountModel> counts = new ArrayList<>();
            for (Integer roomId : totalCounts.keySet()) {
                Integer count = totalCounts.get(roomId);

                if(gameNodeUsersCount > ccu){
                    count = Math.round(ccu * count / gameNodeUsersCount);
                }

                if (info.length() > 0) {
                    info.append(",");
                }

                counts.add(new RoomUserCountModel(roomId, count));
                info.append(roomId).append(".").append(count);
            }

            userCounts = counts;
            CacheController.getInstance().updateRoomUserCounts(info.toString(), groupId);
        }
    }

    public int getRoomUserCount(int roomId){
        int count = 0;
        for (RoomUserCountModel userCount:userCounts) {
            if(userCount.getId() == roomId){
                count = userCount.getCount();
                break;
            }
        }

        return count;
    }
}
