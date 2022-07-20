package com.mynet.shared.user;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mynet.gameserver.enums.ErrorCode;
import com.mynet.gameserver.enums.InfoCode;
import com.mynet.observer.RequestObserverCenter;
import com.mynet.proxyserver.network.xml.LoginXmlUtils;
import com.mynet.proxyserver.observer.*;
import com.mynet.proxyserver.processors.ChangeGameNodeProcessor;
import com.mynet.shared.config.ServerConfiguration;
import com.mynet.shared.network.GameCommands;
import com.mynet.shared.network.InvalidServerMessage;
import com.mynet.shared.network.NetworkMessage;
import com.mynet.shared.resource.db.DBController;
import com.mynet.shared.resource.db.NotificationUrlsController;
import com.mynet.shared.response.*;
import com.mynet.shared.types.RequestType;
import com.mynet.shared.utils.Utils;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.mynet.proxyserver.ConnectionClose;
import com.mynet.proxyserver.login.LoginQueue;
import com.mynet.proxyserver.login.LoginTask;
import com.mynet.proxyserver.login.UserConnectionHandler;
import com.mynet.proxyserver.model.LoginRequest;
import com.mynet.proxyserver.network.StringUtil;
import com.mynet.proxyserver.user.UserModel;
import com.mynet.shared.launchers.ProxyServerLauncher;
import com.mynet.shared.config.ServerGlobalVariables;
import com.mynet.shared.model.TournamentLevelController;
import com.mynet.shared.node.Node;
import com.mynet.shared.node.NodeController;
import com.mynet.shared.resource.CacheController;
import com.mynet.shared.types.ConnectionCloseType;
import com.mynet.shared.types.ServerType;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class.getName());

    private final ConcurrentHashMap<String, ProxyUser> users;
    private NodeController nodeController;
    private CacheController cacheController;
    private LoginQueue loginQueue;
    private Gson gson;
    private int totalUsersSize;
    private long lastUserSizeUpdate;
    private ServerType serverType;
    private static UserController INSTANCE;

    private long roomUserCountUpdateTime = -1;
    private String roomUserCounts = "";

    private String groupId;

    public static UserController getInstance() {
        return INSTANCE;
    }

    private ServerGlobalVariables serverGlobalVariables;

    private UserController(NodeController nodeController, LoginQueue loginQueue, ServerType serverType, String groupId) {
        this.nodeController = nodeController;
        this.cacheController = CacheController.getInstance();
        this.loginQueue = loginQueue;
        this.serverType = serverType;
        this.users = new ConcurrentHashMap<>();
        this.gson = new Gson();
        this.serverGlobalVariables = ServerGlobalVariables.getInstance();
        this.groupId = groupId;

        NotificationUrlsController.init();

        addRedisEventListeners();
        addRequestObservers();
    }

    public void addRequestObservers() {
        RequestObserverCenter center = RequestObserverCenter.getInstance();
        center.addObserver(RequestType.BONUS, new BonusRequest());
        center.addObserver(RequestType.GAME, new GameRequest());
        center.addObserver(RequestType.TABLE, new TableRequest());
        center.addObserver(RequestType.SOCIAL, new SocialRequest());
        center.addObserver(RequestType.QUEST, new QuestRequest());
        center.addObserver(RequestType.MATCH, new MatchRequest());
        center.addObserver(RequestType.SERVER, new ProxyRequest());
        center.addObserver(RequestType.CHAT, new ChatRequest());
    }

    public void addRedisEventListeners() {

        CacheController cache = CacheController.getInstance();

        cache.listenVipUpdateEvents((charSequence, id) -> {
            try {
                String userId = (String) id;
                ProxyUser user = getUser(userId);
                if (user == null) return;

                int status = DBController.getInstance().getUserVipStatus(userId);
                if (status == -1) return;

                user.setVip(status);

                user.updateVip(true);
                NetworkMessage networkMessage = new NetworkMessage(user.getId(), GameCommands.VIP_STATUS_UPDATED, gson.toJson(new UpdateVipStatusResponse(status == 1)));
                user.send(networkMessage);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        });
        cache.listenTicketUpdateEvents((charSequence, id) -> {
            try {
                String userId = (String) id;
                ProxyUser user = getUser(userId);
                if (user == null) return;

                int tickets = DBController.getInstance().getUserTickets(userId);
                user.updateTicket(tickets);

                UpdateTicketResponse dataResponse = new UpdateTicketResponse(user.getTickets(), UpdateTicketResponse.UpdateTicketReason.PURCHASE);

                NetworkMessage networkMessage = new NetworkMessage(user.getId(), GameCommands.USER_TICKET_UPDATED, gson.toJson(dataResponse));
                user.send(networkMessage);

            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        });
        cache.listenMoneyUpdateEvents((charSequence, id) -> {
            try {
                String userId = (String) id;
                ProxyUser user = getUser(userId);
                if (user == null) return;

                long money = DBController.getInstance().getUserMoney(userId);
                user.updateMoney(money);

                NetworkMessage networkMessage = new NetworkMessage(user.getId(), GameCommands.USER_RECEIVED_MONEY, NetworkMessage.getGson().toJson(new UserReceivedMoneyResponse(money)));
                user.send(networkMessage);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        });

        //info = platform + ":" + version
        cache.listenForceUpdate((charSequence, info) -> {
            String[] data = info.split(":");
            String platform = data[0];
            String version = data[1];

            for(ProxyUser user:users.values()){

                if(user.platform.equals(platform) && Utils.isOldVersion(version,user.getApplicationVersion())){
                    String url = NotificationUrlsController.getInstance().getUrl("force_update_"+platform);

                    if(url != null) {
                        NetworkMessage networkMessage = new NetworkMessage(user.getId(), GameCommands.INFO, NetworkMessage.getGson().toJson(new InfoResponse(InfoCode.FORCE_UPDATE, url, false)));
                        user.send(networkMessage);
                    }
                }
            }
        });

        cache.listenSystemNotificationMessage((charSequence, info) -> {
            String[] data = info.split("-");
            if (data.length < 2) return;

            String id = data[0];
            String message = data[1];

            ProxyUser user = getUser(id);
            if (user == null) return;

            NetworkMessage networkMessage = new NetworkMessage(user.getId(), GameCommands.INFO, NetworkMessage.getGson().toJson(new InfoResponse(message, InfoCode.SYSTEM_MESSAGE)));
            user.send(networkMessage);
        });

        cacheController.listenUpdateSessionId((charSequence, info) -> {
            String[] data = info.split(":");

            if (data.length < 2) return;

            String id = data[0];
            String sessionId = data[1];

            ProxyUser user = getUser(id);
            if (user == null) return;

            user.setAnalyticsSessionId(sessionId);
        });
    }


    public void login(NetworkMessage request, Channel channel, UserConnectionHandler handler) {

        if (request.getCmd() == GameCommands.LOGIN) {
            LoginRequest loginReq = null;
            try {
                loginReq = NetworkMessage.getGson().fromJson(request.getData(), LoginRequest.class);
            } catch (JsonSyntaxException ex) {
                logger.error(UserController.class.getName(), ex);

                NetworkMessage message = new NetworkMessage(GameCommands.ERROR);
                message.setDataAsJSON(new ErrorResponse(ErrorCode.INVALID_LOGIN));
                channel.writeAndFlush(message, channel.voidPromise());
            }

            if (loginReq != null) {

                if (!loginReq.getPlatform().equals("ios") && !loginReq.getPlatform().equals("android") && !loginReq.getPlatform().equals("huawei")) {
                    loginReq.setPlatform(LoginXmlUtils.GetIsOnCanvas(loginReq.getId(), loginReq.getPlatform()));
                }

//                boolean validLogin = LoginXmlUtils.IsValidLogin(loginReq.getId(), loginReq.getSecretKey(), loginReq.getPlatform());
//
//                if (!validLogin) {
//                    NetworkMessage message = new NetworkMessage(GameCommands.ERROR);
//                    message.setDataAsJSON(new ErrorResponse(ErrorCode.INVALID_LOGIN));
//                    channel.writeAndFlush(message, channel.voidPromise());
//                    channel.close();
//                    return;
//                }

                LoginTask task = new LoginTask(loginReq, channel, handler);
                boolean isSuccess = UserController.getInstance().getLoginQueue().add(task);
                if (!isSuccess) {
                    channel.close();
                }

            } else {
                NetworkMessage message = new NetworkMessage(GameCommands.ERROR);
                message.setDataAsJSON(new ErrorResponse(ErrorCode.INVALID_LOGIN));
                channel.writeAndFlush(message, channel.voidPromise());

                // close it if we're not getting login request as first
                channel.close();
            }
        }
    }

    public LoginQueue getLoginQueue() {
        return loginQueue;
    }

    public static void init(NodeController nodeController, ServerType serverType, String groupId) {
        if (INSTANCE == null) {
            UserController u = new UserController(nodeController, LoginQueue.getInstance(), serverType, groupId);
            INSTANCE = u;
            u.startTimeoutTask();
        }
    }

    private void startTimeoutTask() {
        Timer timer = new Timer(true);
        TimerTask task = new UserController.UserTimeoutTask();
        timer.scheduleAtFixedRate(task, 10000, 5000);
    }

    public ProxyUser getUser(String id) {
        return this.users.get(id);
    }

    public void removeUser(String userId) {
        ProxyUser user = getUser(userId);
        if (user != null) {
            removeUser(user);
        }
    }

    public void removeUser(ProxyUser user) {

        user.userRemoved();

        NetworkMessage disconnectMessage = new NetworkMessage(GameCommands.SOCKET_DISCONNECT);
        disconnectMessage.setData("Bağlantın Koptu!");
        user.send(disconnectMessage);

        nodeController.removeUserFromNodes(user);

        clearChatHistory(user);

        makeUserOfflineAtCache(user);

        removeUserFromNode(user);
    }

    public void removeUserFromNode(ProxyUser user) {
        if (user != null) {
            if (user.getChannel() != null) {
                user.getChannel().close();
            }

            this.users.remove(user.getId());
        }
    }

    /**
     * This method called when user logged in another proxy
     */
    public void removeUserFromProxy(ProxyUser user) {
        user.userRemoved();

        NetworkMessage disconnectMessage = new NetworkMessage(GameCommands.SOCKET_DISCONNECT);
        disconnectMessage.setData("Bağlantın Koptu!");
        user.send(disconnectMessage);

        removeUserFromNode(user);

    }

    public void addUser(ProxyUser user) {
        this.users.put(user.getId(), user);

        int lobbyId = ServerConfiguration.getInt("lobby.id", -1);

        cacheController.addUser(user, lobbyId);
        cacheController.setProxyNode(user.getId(), ProxyServerLauncher.currentNode.getId());
        cacheController.makeUserOnline(user.getId());
    }

    public void sendNetworkMessage(NetworkMessage message, String userId) {
        ProxyUser user = getUser(userId);
        user.send(message);
    }

    public void sendAuthorize(ProxyUser user, boolean reconnect) {

        NetworkMessage response = new NetworkMessage();

        response.setCmd(GameCommands.LOGIN);
        UserLoginResponse.Builder loginResponseBuilder = new UserLoginResponse.Builder();
        loginResponseBuilder.setReconnect(reconnect);
        loginResponseBuilder.setPrivateChat(user.userModel.privateChat);
        loginResponseBuilder.setGoProfile(user.userModel.goProfile);
        loginResponseBuilder.setTickets(user.getTickets());
        loginResponseBuilder.setUserMoney(user.getMoney());
        loginResponseBuilder.setIsVip(user.isVip());
        loginResponseBuilder.setServerType(user.getServerType());

        if(user.getServerType() == ServerType.TOURNAMENT){

            //Send tournament info to only users with old app versions
            String setting = "tournamentUpdateVersion:" + user.getPlatform();
            String tournamentOldVersion = ServerGlobalVariables.getInstance().getString(setting, "");

            if (!user.getPlatform().equals("web") && Utils.isOldVersion(tournamentOldVersion, user.getApplicationVersion())) {
                loginResponseBuilder.setTournamentList(TournamentLevelController.getInstance().getTournamentForUserInit(user));
            } else {
                TournamentLevelController.getInstance().setUserTournamentStats(user);
            }
        }

        NetworkMessage networkMessage = new NetworkMessage(user.getId(), GameCommands.LOGIN, NetworkMessage.getGson().toJson(loginResponseBuilder.build()));
        user.send(networkMessage);
    }

    private String getRoomUserCounts() {
        long current = System.currentTimeMillis();
        if (roomUserCountUpdateTime + 60000 < current) {
            roomUserCounts = cacheController.getRoomUserCounts(groupId);
            roomUserCountUpdateTime = current;
        }

        return roomUserCounts;
    }

    private String getAuthUserInfo(ProxyUser user) {
        UserModel userModel = user.getUserModel();
        String message = StringUtil.correctTurkish(userModel.firstName + ";"
                + userModel.lastName + ";"
                + "n" + ";"
                + "null" + ";" //profile url
                + userModel.money + ";"
                + 1 + ";" // experience
                + userModel.gamesWon + ";"
                + userModel.gamesTotal + ";"
                + StringUtil.getHashCodeString(userModel.fuid) + ";"
                + 1 + ";" // socket id
                + userModel.potMax + ";"
                + "ÇanakOkey" + ";"
                + 0 + ";" //sound
                + 0 + ";" // invite
                + 0 + ";"
                + (userModel.paid ? 1 : 0) + ";"
                + (userModel.vip ? 1 : 0) + ";"
                + userModel.currentGift + ";"
                + 0 + ";" // altin istaka
                + 0 + ";" // go profile
                + 0 + ";" // get laert
                + 0) + ";"
                + 0 + ";"
                + 0 + ";"
                + "3" + ";" // server id
                + 8 + ";"//unnecessary for tournament
                + 0 + ";" // time bonus total
                + "1,180,250&2,540,500&3,1140,1000&4,2340,1500&5,4740,2000" + ";" // time bonus stuff
                + userModel.getTournamentBadge();

        return message;
    }

    public void makeUserOfflineAtCache(ProxyUser user) {
        if (cacheController.getUserProxyNode(user.getId()) == ProxyServerLauncher.currentNode.getId()) {
            cacheController.setProxyNode(user.getId(), -1);
         //   cacheController.setTableId(user.getId(), -1);
            cacheController.makeUserOffline(user.getId());
        }
    }

    public void clearChatHistory(ProxyUser user) {
        cacheController.clearChatHistory(user.getId());
    }

    public void closeConnection(ProxyUser user, ConnectionCloseType closeType) {
        final Channel channel = user.getChannel();
        String msg = "Bağlantınız kapatıldı.";
        switch (closeType) {
            case ANOTHER_CLIENT:
                msg = "Başka bir yerden giriş yaptınız.";
                break;
        }

        ConnectionClose connectionClose = new ConnectionClose(msg);
        NetworkMessage res = new NetworkMessage(GameCommands.SOCKET_DISCONNECT);

        Gson gson = new Gson();
        String data = gson.toJson(connectionClose);
        res.setData(data);

        if (channel != null) {
            channel.writeAndFlush(res).addListener(future -> channel.close());
        }

    }

    public void processRequest(ProxyUser user, NetworkMessage request) {

        convertRequestType(user, request);

        int maxRequestCount = serverGlobalVariables.getInt("max_request_limit_in_second", 20);
        int processExceededRequest = serverGlobalVariables.getInt("process_exceeded_requests", 0);
        int blackListLimit = serverGlobalVariables.getInt("requests_black_list_limit", 60);
        boolean processRequest = true;
        int requestCount = 1;
        if (System.currentTimeMillis() - user.getLastRequestTime() > 1000) {
            requestCount = user.getTotalRequestCount() - user.getLastRequestCount();
            if (requestCount > maxRequestCount) {
                // ALARM
                logger.error(user.getId() + " REQUEST LIMIT EXCEEDED - request count in second: " + requestCount + " " + System.currentTimeMillis());
                if (processExceededRequest == 0) {
                    processRequest = false;
                }
            }
            user.setLastRequestTime(System.currentTimeMillis());
            user.setLastRequestCount(user.getTotalRequestCount());
        }
        user.setTotalRequestCount(user.getTotalRequestCount() + 1);

        user.setLastActionTime(System.currentTimeMillis());
        if (processRequest) {
            RequestType requestType = request.getCmd().getType();

            if(requestType != null){
                RequestObserverCenter.getInstance().emit(requestType, new RequestData(user, request));
            }

            for(RequestType type: request.getCmd().getTags()){
                RequestObserverCenter.getInstance().emit(type, new RequestData(user, request));
            }
        } else {
            removeUser(user);
            if (requestCount > blackListLimit) {
                cacheController.addToBlackList(user.getId());
            } else {
                cacheController.addToSuspectList(user.getId());
            }
        }
    }

    private String getGameNodeFromRequest(String value, int requiredIndex) {
        try {
            String[] arr = StringUtil.processRawString(value, ";");
            if (arr.length > requiredIndex && !arr[requiredIndex].equals("-1")) {
                return arr[requiredIndex];
            } else {
                return null;
            }
        } catch (Exception e) {

        }

        return null;
    }

    private void convertRequestType(ProxyUser user, NetworkMessage request) {
        try {
            switch (request.getCmd()) {
                case SIT_TABLE_2:
                    String sitNode = getGameNodeFromRequest(request.getData(), 2);
                    if (sitNode != null) {
                        sendChangeNodeRequest(user.id, sitNode);
                        request.setCmd(GameCommands.SIT_NODE_TABLE);
                    }
                    break;
                case JOIN_AN_AUDIENCE:
                    String joinNode = getGameNodeFromRequest(request.getData(), 1);
                    if (joinNode != null) {
                        sendChangeNodeRequest(user.id, joinNode);

                        request.setCmd(GameCommands.JOIN_NODE_TABLE);
                    }
                    break;
                case GO_BEHIND_FRIEND:
                    ProxyUser friend = getUser(request.getData());
                    int gameNode = -1;
                    if (friend == null) {
                        gameNode = CacheController.getInstance().getUserGameNode(request.getData());
                    } else {
                        gameNode = friend.getGameId();
                    }

                    sendChangeNodeRequest(user.getId(), gameNode + "");

                    break;

                case ENTER_ROOM_FRIEND:
                    String friendNode = getGameNodeFromRequest(request.getData(), 1);
                    if (friendNode != null) {
                        sendChangeNodeRequest(user.id, friendNode);
                        request.setCmd(GameCommands.ENTER_ROOM_2);
                    }
                    break;
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    private void sendChangeNodeRequest(String userId, String node) throws InvalidServerMessage {
        ChangeGameNodeProcessor processor = new ChangeGameNodeProcessor();
        processor.process(new NetworkMessage(userId, GameCommands.CHANGE_GAME_NODE, node));
    }

    public boolean isChatMessageLimitExceed(ProxyUser user, NetworkMessage request) {
        if(request.getCmd() == GameCommands.TABLE_CHAT){
            long lastChatMessageTime = user.getLastChatMessageTime();
            long now = System.currentTimeMillis();
            long dif = now - lastChatMessageTime;
            int chatMessageCount = user.getChatMessageCount();

            if(dif < 1000 && chatMessageCount >= 2){
                return true;
            }

            if(dif >= 1000){
                chatMessageCount = 0;
                user.setChatMessageCount(chatMessageCount);
            }

            user.setLastChatMessageTime(now);
            user.setChatMessageCount(chatMessageCount + 1);
        }
        return false;
    }

    public void sendServerMessageToUser(ProxyUser user, String msg) {
        user.sendServerMessage(msg);
    }

    public Iterator<ProxyUser> getUserIterator() {
        return this.users.values().iterator();
    }

    public Node getNode(int nodeId) {
        return nodeController.getNode(nodeId);
    }

    public Collection<Node> getNodes() {
        return nodeController.getNodes();
    }

    public void addToGameNode(int nodeId, ProxyUser user) {
        Node node = getNode(nodeId);
        user.setGameId(nodeId);
        user.setGameNode(node);
        node.addUser(user);
    }

    class UserTimeoutTask extends TimerTask {
        @Override
        public void run() {

            long start = System.currentTimeMillis();
            try {
                long afk_timeout = serverGlobalVariables.getInt("afk_timeout", 600000);
                long no_connection_timeout = serverGlobalVariables.getInt("no_connection_timeout", 40000);
                long ping_timeout = serverGlobalVariables.getInt("ping_timeout", 60000);

                for (ProxyUser user : users.values()) {
                    long time = System.currentTimeMillis() - user.getLastActionTime();
                    long noChannelTime = System.currentTimeMillis() - user.getLastConnectionLostTime();
                    long lastPingTime = user.getLastPingTime(); // 0 means, no ping sent, ignore
                    long pingResponseTime = System.currentTimeMillis() - lastPingTime;

                    if (user.getChannel() == null) {
                        // short timeout w/o connection
                        if (noChannelTime > 0 && noChannelTime > no_connection_timeout) {
                            logger.info("removing player for no connection");
                            removeUser(user);
                        }
                    } else {
                        // long timeout for AFK
                        if (time > afk_timeout) {
                            logger.info("removing player for afk");
                            removeUser(user);
                        }

//                        if (lastPingTime > 0 && pingResponseTime > ping_timeout) {
//                            logger.info("removing player for not responding to ping");
//                            removeUser(user);
//                        }
                    }
                }

            } finally {
                long end = System.currentTimeMillis();
                long elapsed = end - start;
                if (elapsed > 3000) {
                    logger.error("UserTimeout Task run time = " + elapsed);
                }
            }

        }
    }

    public Node getAvailableGameNode(ServerType serverType) {
        return nodeController.getAvailableGameNode(serverType);
    }

    public ServerType getServerType() {
        return serverType;
    }

    public void setUserNodes(ProxyUser user){
        nodeController.setUserNodes(user);
    }

    public NodeController getNodeController() {
        return nodeController;
    }
}
