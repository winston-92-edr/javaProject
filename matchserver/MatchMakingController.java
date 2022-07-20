package com.mynet.matchserver;

import com.mynet.matchserver.model.GameTypeInfo;
import com.mynet.proxyserver.user.UserModel;
import com.mynet.shared.config.ServerGlobalVariables;
import com.mynet.shared.node.NodeData;
import com.mynet.shared.resource.CacheController;
import com.mynet.shared.types.ServerType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.mynet.shared.connection.NodeToProxy;
import com.mynet.shared.resource.db.DBController;
import com.mynet.shared.types.GameType;

import javax.naming.CannotProceedException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;


public class MatchMakingController {
    private static Logger logger = LoggerFactory.getLogger(MatchMakingController.class);
    private static MatchMakingController INSTANCE;
    private ConcurrentHashMap<GameType, MatchMakingQueue> queues;
    private ConcurrentHashMap<String, GameUser> users;
    private NodeToProxy nodeToProxy;
    private GameTypeInfo[] gameTypeInfoList;
    private ServerGlobalVariables globalVariables;
    private ArrayList<NodeData> gameNodes;
    private int gameNodeRoundRobin = 0;
    private String groupId;

    public static void init(GameTypeInfo[] infos, String groupId){
        if(INSTANCE == null){
            INSTANCE = new MatchMakingController(infos, groupId);
        }
    }

    private MatchMakingController(GameTypeInfo[] infos, String groupId) {
        queues = new ConcurrentHashMap<>();
        users = new ConcurrentHashMap<>();
        nodeToProxy = new NodeToProxy();
        gameTypeInfoList = infos;
        globalVariables = ServerGlobalVariables.getInstance();
        this.groupId =  groupId;
        gameNodes = new ArrayList<>();
        addGameNodes();

        for (GameTypeInfo info : infos) {
            MatchMakingQueue queue = new MatchMakingQueue(info, this);
            queues.put(info.getGameType(), queue);
        }

        addRedisEventListeners();
    }

    public GameTypeInfo getGameTypeInfo(GameType type){
        for (GameTypeInfo info : gameTypeInfoList) {
            if(info.getGameType() == type){
                return info;
            }
        }

        return null;
    }

    public static MatchMakingController getInstance(){
        return INSTANCE;
    }

    public NodeToProxy getNodeToProxy() {
        return nodeToProxy;
    }

    public boolean isInMaintenance() {
        String mode = globalVariables.getString("TOURNAMENT_MAINTENANCE_MODE", "false");
        return mode.equals("true");
    }

    public GameUser getUser(String id) {
        return users.get(id);
    }
    public boolean addMatchRequest(MatchRequest request){
        MatchMakingQueue queue = queues.get(request.getGameTypeInfo().getGameType());
        if(queue != null){
            queue.addMatch(request);
            return true;
        }else{
            logger.error("Matchmaking queue NOT FOUND!");
        }
        return false;
    }

    public boolean cancelMatchRequest(MatchRequest request){
        MatchMakingQueue queue = queues.get(request.getGameTypeInfo().getGameType());
        if(queue != null){
            queue.removeMatch(request);
            return true;
        }else{
            logger.error("Matchmaking queue NOT FOUND!");
        }
        return false;
    }

    public boolean addUser(GameUser user) {
        String userID = user.getId();

        if (users.containsKey(userID)) {
            return false;
        }

        users.put(userID, user);


        return true;
    }

    public void createUser(String userId) throws CannotProceedException {
        removeUser(userId);

        //TODO: check redis first
        UserModel userModel = DBController.getInstance().getUser(userId);
        GameUser user = new GameUser(userModel);
        addUser(user);
    }

    public void removeUser(String userId) {
        users.remove(userId);
    }

    public void fixUser(String userId) {
        if(!users.containsKey(userId)){
            try {
                createUser(userId);
            }catch (CannotProceedException e){
                logger.error(e.getMessage(), e);
            }
        }
    }

    public NodeData getGameNode(){
        if(gameNodeRoundRobin >= gameNodes.size()){
            gameNodeRoundRobin = 0;
        }
        return gameNodes.get(gameNodeRoundRobin++);

    }

    public void addGameNodes(){
        ArrayList<NodeData> gameNodeArray = CacheController.getInstance().getGameNodes(groupId);

        for(NodeData nodeData: gameNodeArray){
            if(gameNodes == null) return;
            if (nodeData.getGroupId().equals(groupId) && nodeData.getServerType().equals(ServerType.TOURNAMENT)) {

                if(gameNodes.stream().filter(x->x.getId() == nodeData.getId()).count() == 0){
                    gameNodes.add(nodeData);
                }
                logger.info("Game node " + nodeData.getId() + " added!");
            };
        }
    }

    private void addRedisEventListeners() {
        CacheController.getInstance().listenGameNodeDeleteEvent((charSequence, nodeId) -> {
            if(gameNodes == null) return;
            gameNodes.removeIf(nodeData -> nodeData.getId() == nodeId);
        });

        CacheController.getInstance().listenGameNodeAddEvent((charSequence, nodeData) -> {
            if(gameNodes == null) return;
            if (nodeData.getGroupId().equals(groupId) && nodeData.getServerType().equals(ServerType.TOURNAMENT)) {

                if(gameNodes.stream().filter(x->x.getId() == nodeData.getId()).count() == 0){
                    gameNodes.add(nodeData);
                }
                logger.info("Game node " + nodeData.getId() + " added!");
            };

        });
    }
}
