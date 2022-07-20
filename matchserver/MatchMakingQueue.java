package com.mynet.matchserver;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.google.gson.Gson;
import com.mynet.matchserver.model.GameTypeInfo;
import com.mynet.matchserver.response.MatchingResponse;
import com.mynet.shared.config.ServerConfiguration;
import com.mynet.shared.node.NodeData;
import com.mynet.shared.utils.Utils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.mynet.shared.model.UserTournamentModel;
import com.mynet.shared.network.GameCommands;
import com.mynet.shared.network.NetworkMessage;
import com.mynet.shared.resource.CacheController;
import com.mynet.shared.model.BasicUserModel;

public class MatchMakingQueue extends Thread {
    private static Logger logger = LoggerFactory.getLogger(MatchMakingQueue.class);
    private final Lock queueLock;
    private int duelTimeout = 20000;
    private int duelMMWait = 2000;
    private BlockingQueue<MatchRequest> queue;
    private GameTypeInfo info;
    private MatchMakingController matchMakingController;
    private Gson gson;

    public MatchMakingQueue(GameTypeInfo info, MatchMakingController controller) {
        this.info = info;
        this.matchMakingController = controller;
        queueLock = new ReentrantLock();
        queue = new PriorityBlockingQueue();
        gson = new Gson();
        start();
    }

    public void addMatch(MatchRequest request){
        try {
            queueLock.lock();
            queue.offer(request);
            logger.info("new request add : " + request.getUser().getBasicUser().getName());
        }finally {
            queueLock.unlock();
        }
    }

    public void removeMatch(MatchRequest request) {
        try {
            queueLock.lock();
            queue.remove(request);
            logger.info("request remove : " + request.getUser().getBasicUser().getName());
        }finally {
            queueLock.unlock();
        }
    }

    @Override
    public void run() {
        long lastQueueProcessTime = 0;

        while (true) {

            try {

                if ((System.currentTimeMillis() - lastQueueProcessTime) > duelMMWait) {
                    lastQueueProcessTime = System.currentTimeMillis();
                    try {
                        queueLock.lock();
                        processMatching();

                    } finally {
                        queueLock.unlock();
                    }
                }

                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {

                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }

        }
    }

    private void processMatching() {
        while (queue.size() >= info.getPlayerCount()){
            List<MatchRequest> requests = new ArrayList<>(info.getPlayerCount());
            for (int i = 0; i < info.getPlayerCount(); i++) {
                MatchRequest request = queue.poll();
                requests.add(request);
            }
            match(requests);
            addQueueTime(requests);
        }

        // timeout check
        int size = queue.size();
        if(size < info.getPlayerCount() && size > 0){
            List<MatchRequest> requestList = new ArrayList<>();
            for (int i = 0; i < size; i++) {
                MatchRequest request = queue.poll();
                long waitingTime = System.currentTimeMillis() - request.getMatchmakingStartTime();
                if(waitingTime < duelTimeout){
                    requestList.add(request);
                }else{
                    sendCancelMatch(request);
                }
            }

            for (MatchRequest req: requestList) {
                queue.offer(req);
            }
        }
    }

    private void addQueueTime(List<MatchRequest> requests) {
        long longestTime = 0;
        for (int i = 0; i < requests.size(); i++) {
            long queueTime = System.currentTimeMillis() - requests.get(i).getMatchmakingStartTime();
            if(queueTime > longestTime){
                longestTime = queueTime;
            }
        }


    }

    private void match(List<MatchRequest> requests) {
        //check is available match;
        String sameIpUserId = null;
        boolean checkSameIp = Utils.checkSameIp();

        if(checkSameIp){
            for(MatchRequest request: requests){
                if(requests.stream().filter(x -> x.getUser().getIp().equals(request.getUser().getIp())).count() > 1){
                    sameIpUserId = request.getUser().getfuid();
                    break;
                }
            }
        }

        if(sameIpUserId != null){
            for(MatchRequest request: requests){
                if(request.getUser().getfuid().equals(sameIpUserId)){

                    logger.warn("Same ip cancel matchmaking");
                    sendCancelMatch(request);
                }else{
                    addMatch(request);
                }
            }

            return;
        }


        int side = 0;
        int tableId = CacheController.getInstance().incAndGetTableCounter();
        BasicUserModel[] users = new BasicUserModel[requests.size()];
        for (int i = 0; i <requests.size() ; i++) {
            MatchRequest request = requests.get(i);
            GameUser user = request.getUser();
            user.setTournamentId(request.getTournamentId());

            users[i] = user.getBasicUser();
            users[i].setTournamentId(request.getTournamentId());

            try {
                user.fillTournamentEvent(user.getTournamentId(), true, tableId, 0, 0);
                user.endTournamentEvent();
            }catch (Exception e){
                logger.error(e.getMessage(), e);
            }
        }

        MatchingResponse gameRequest = new MatchingResponse.Builder()
                .sideCount(info.getPlayerCount())
                .tableId(tableId)
                .users(users)
                .build();

        int gameNodeId = sendMatchRequestToGameServer(gameRequest);
        if(gameNodeId != -1){
            for (MatchRequest request : requests) {
                NetworkMessage response = new NetworkMessage(GameCommands.TOURNAMENT_MATCHMAKING);
                MatchingResponse matchingResponse = new MatchingResponse.Builder()
                        .sideCount(request.getGameTypeInfo().getPlayerCount())
                        .side(side)
                        .tournamentId(request.getTournamentId())
                        .lookingForOp(false)
                        .tableId(tableId)
                        .users(users)
                        .gameNodeId(gameNodeId)
                        .build();
                response.setSuccess(true);
                response.setDataAsJSON(matchingResponse);
                matchMakingController.getNodeToProxy().addServerMessage(response, request.getUser());
                side++;
            }
        }else {
            for (MatchRequest request: requests) {
                sendCancelMatch(request);
            }
        }

        //matchMakingController.getNodeToProxy().addServerMessage(response, proxyId);
    }

    private void sendCancelMatch(MatchRequest request) {
        GameUser user = request.getUser();

        if(matchMakingController.getUser(user.getId()) != null) {

            UserTournamentModel utm = user.getTournament(request.getTournamentId());
            user.fillTournamentEvent(request.getTournamentId(), false, -1, 0,0);
            user.endTournamentEvent();

            NetworkMessage response = new NetworkMessage(GameCommands.CANCEL_MATCHMAKING);
            response.setDataAsJSON(utm);
            response.setSuccess(true);
            matchMakingController.getNodeToProxy().addServerMessage(response, user);

        }
    }

    private boolean isAvailable(List<MatchRequest> requests) {
//        Set<String> ips = new HashSet<>();
//        for(MatchRequest request: requests){
//            ips.add(request.getUser().getIp());
//        }
//
//        return ips.size() == info.getPlayerCount();

        return true;
    }

    private int sendMatchRequestToGameServer(MatchingResponse match){

        try {
            NodeData gameNode = matchMakingController.getGameNode();
            logger.info("GAME NODE ID:" + gameNode.getId());
            String postUrl = "http://" + gameNode.getHost() + ":" + gameNode.getHttpPort() + "/table/start";
            CloseableHttpClient httpClient = HttpClientBuilder.create().build();
            HttpPost post = new HttpPost(postUrl);
            StringEntity postingString = new StringEntity(gson.toJson(match));
            post.setEntity(postingString);
            post.setHeader("Content-type", "application/json");
            HttpResponse response = httpClient.execute(post);
            httpClient.close();
            return response.getStatusLine().getStatusCode() == HttpStatus.SC_OK ? gameNode.getId() : -1;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return -1;
    }


}
