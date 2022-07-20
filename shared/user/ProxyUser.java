package com.mynet.shared.user;

import com.mynet.gameserver.actions.GameAction;
import com.mynet.shared.network.NetworkMessage;
import com.mynet.shared.resource.db.DBController;
import com.mynet.shared.response.ClaimAwardResponse;
import com.mynet.shared.response.UserReceivedMoneyResponse;
import com.mynet.shared.types.ClaimAwardType;
import com.mynet.shared.types.ServerType;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.mynet.proxyserver.login.UserConnectionHandler;
//import com.mynet.proxyserver.network.ProxyMessage;
import com.mynet.proxyserver.user.UserModel;
import com.mynet.shared.launchers.ProxyServerLauncher;
import com.mynet.shared.logs.TournamentEventLog;
import com.mynet.shared.model.TournamentBadge;
import com.mynet.shared.model.TournamentModel;
import com.mynet.shared.model.UserTournamentModel;
import com.mynet.shared.network.GameCommands;
import com.mynet.shared.network.NetworkMessageWrapper;
import com.mynet.shared.node.Node;
import com.mynet.shared.resource.CacheController;
import com.mynet.shared.response.ServerMessageResponse;
import com.mynet.socialserver.SocialController;

public class ProxyUser extends User {
    Logger logger = LoggerFactory.getLogger(ProxyUser.class.getName());


    private Node gameNode;
    private ServerType serverType;

    private UserConnectionHandler userConnectionHandler;

    private Channel channel;

    private long lastPingTime; // 0 means no ping sent yet, after getting ping it should be set to 0
    private long lastConnectionLostTime; // 0 means connection not lost lately

    private long lastChatMessageTime; // for block chat abuse
    private long lastUserOptionUpdate;

    private long lastActionTime;
    private long lastActiveTime;
    private long sessionStartTime;
    private long userLoginTime;
    private long lastRequestTime;
    private int lastRequestCount;
    private int totalRequestCount;

    private GameAction lastGameAction;

    private int chatMessageCount;

    private Node socialNode;

    private boolean connected;

    public ProxyUser(UserModel model) {
        super(model);

        this.lastRequestTime = System.currentTimeMillis();
        this.lastRequestCount = 1;
        this.totalRequestCount = 1;
        this.userLoginTime = System.currentTimeMillis();
        this.sessionStartTime = System.currentTimeMillis();
        this.lastActionTime = System.currentTimeMillis();
    }

    public UserConnectionHandler getUserConnectionHandler() {
        return userConnectionHandler;
    }

    public void setUserConnectionHandler(UserConnectionHandler userConnectionHandler) {
        this.userConnectionHandler = userConnectionHandler;
    }

    public long getLastConnectionLostTime() {
        return lastConnectionLostTime;
    }

    public void setLastConnectionLostTime(long lastConnectionLostTime) {
        this.lastConnectionLostTime = lastConnectionLostTime;
    }

    public long getLastChatMessageTime() {
        return lastChatMessageTime;
    }

    public void setLastChatMessageTime(long lastChatMessageTime) {
        this.lastChatMessageTime = lastChatMessageTime;
    }

    public long getLastPingTime() {
        return lastPingTime;
    }

    public void setLastPingTime(long lastPingTime) {
        this.lastPingTime = lastPingTime;
    }
    public long getLastActionTime() {
        return lastActionTime;
    }

    public void setLastActionTime(long lastActionTime) {
        this.lastActionTime = lastActionTime;
    }


    public long getUserLoginTime() {
        return userLoginTime;
    }

    public void setUserLoginTime(long userLoginTime) {
        this.userLoginTime = userLoginTime;
    }
    public int getRoomId() {
        return userModel.roomID;
    }

    public void setRoomId(int roomId) {
        this.userModel.roomID = roomId;
    }

    public int getTableId() {
        return userModel.tableID;
    }

    public void setTableId(int tableId) {
        this.userModel.tableID = tableId;
    }

    public int getProxyId() {
        return userModel.proxyID;
    }

    public void setProxyId(int proxyId) {
        this.userModel.proxyID = proxyId;
    }

    public int getGameId() {
        return userModel.gameID;
    }

    public void setGameId(int gameId) {
        this.userModel.gameID = gameId;
    }

    public int getSocialId() {
        return userModel.socialID;
    }

    public void setSocialId(int socialId) {
        this.userModel.socialID = socialId;
    }

    public int getProxyID() {
        return this.userModel.proxyID;
    }


    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public Node getGameNode() {
        return gameNode;
    }

    public void setGameNode(Node gameNode) {
        this.gameNode = gameNode;
    }

    public UserModel getUserModel() {
        return userModel;
    }

    public void setUserModel(UserModel userModel) {
        this.userModel = userModel;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void send(NetworkMessage message, boolean shouldLog){
        NetworkMessageWrapper wrapper = new NetworkMessageWrapper(message, getChannel());
        ProxyServerLauncher.userMessageQueue.addMessage(wrapper);
    }

    public long getLastRequestTime() {
        return lastRequestTime;
    }

    public void setLastRequestTime(long lastRequestTime) {
        this.lastRequestTime = lastRequestTime;
    }

    public int getTotalRequestCount() {
        return totalRequestCount;
    }

    public void setTotalRequestCount(int totalRequestCount) {
        this.totalRequestCount = totalRequestCount;
    }

    public int getLastRequestCount() {
        return lastRequestCount;
    }

    public void setLastRequestCount(int lastRequestCount) {
        this.lastRequestCount = lastRequestCount;
    }

    public void send(NetworkMessage message) {
        send(message, true);
    }


    public void userRemoved(){

        //TODO: Log remove user

        //Kim ne logluyor burada, bunu okuyan var mı? Consume ediliyor mu?

//        if(cacheController.getUserProxyNode(getId()) == myNodeData.id) {
//            LogQueueData logQueueData = new LogQueueData(LogQueueData.LogQueueDataType.SESSION_DATA,
//                    new LoginData(getId(), getSessionStartTime(), System.currentTimeMillis(), getIp(), getUserModel().platform, getApplicationVersion()), LogQueueData.SESSION_LOG_GROUP_ID, getGameType());
//
//            if (!LogQueue.getInstance().add(logQueueData)) {
//                logger.error("Couldn't add login data to SQS queue, because queue full!");
//            }
//        }

    }




    public void sendServerMessage(String msg) {
        NetworkMessage response = new NetworkMessage(GameCommands.SERVER_MESSAGE);
        response.setDataAsJSON(new ServerMessageResponse(msg));
        send(response);
    }

    public long getSessionStartTime() {
        return sessionStartTime;
    }


    public String getIp() {
        return ip;
    }

    public long getTickets() {
        return userModel.tickets;
    }

    public void setTickets(int tickets) {
        this.userModel.tickets = tickets;
    }

    public void setMoney(long money){
        this.userModel.money = money;
    }

    public long getMoney(){
        return this.userModel.money;
    }

    public void setVip(int val){
        this.userModel.vip = val == 1;
    }

    public boolean isVip(){
        return this.userModel.vip;
    }

    public long getLastUserOptionUpdate() {
        return lastUserOptionUpdate;
    }

    public void setLastUserOptionUpdate(long lastUserOptionUpdate) {
        this.lastUserOptionUpdate = lastUserOptionUpdate;
    }

    public ServerType getServerType() {
        return serverType;
    }

    public void setServerType(ServerType serverType) {
        this.serverType = serverType;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
        CacheController.getInstance().setUserLastPlatform(this.id, platform);
    }

    public void setLastActiveTime(long lastActiveTime) {
        this.lastActiveTime = lastActiveTime;
    }

    public boolean claimTournamentAward(TournamentModel tournament) {
        int tournamentId = tournament.getTournamentId();
        UserTournamentModel levelModel = getTournament(tournamentId);
        if (levelModel == null || !levelModel.isOver() || levelModel.claimed) {
            return false;
        }

        // process award
        long award = levelModel.award;
        int badgeId = tournament.getLevels().get(levelModel.level - 1).getBadge();
        TournamentBadge badge = CacheController.getInstance().getUserTournamentBadge(this.id);

        if (badgeId != 0) {
            if (badge != null) {
                if (badgeId <= badge.getBadgeId()) {
                    badge = new TournamentBadge(tournamentId, badgeId);
                    CacheController.getInstance().setUserTournamentBadge(this.id, badge);
                    this.tournamentBadgeStr = badge.toString();
                }
            } else {
                badge = new TournamentBadge(tournamentId, badgeId);
                CacheController.getInstance().setUserTournamentBadge(this.id, badge);
                this.tournamentBadgeStr = badge.toString();
            }
        }

        String badgeStr = badge != null ? badge.toString() : null;

        boolean success = updateMoneyAndWriteLog("claim_tournament_id_" + tournamentId, levelModel.level, award, true, System.currentTimeMillis());
        CacheController.getInstance().incrementUserTopListScore(getId(),award);

        beginTournamentEvent(TournamentEventLog.Type.CLAIM);
        fillTournamentEvent(tournamentId, success, -1, 0, award);
        endTournamentEvent();

        NetworkMessage networkMessage = new NetworkMessage(GameCommands.USER_RECEIVED_MONEY);
        networkMessage.setSuccess(true);
        networkMessage.setDataAsJSON(new UserReceivedMoneyResponse(userModel.money));

        SocialController socialController = SocialController.getInstance();
        socialController.getNodeToProxy().addServerMessage(networkMessage,this);

        NetworkMessage response = new NetworkMessage(GameCommands.CLAIM_AWARD);
        ClaimAwardResponse claimResponse = new ClaimAwardResponse.Builder(ClaimAwardType.TOURNAMENT)
                .setAmount(award)
                .setFinalMoney(userModel.money)
                .setConsumed(true)
                .setAwardId(tournament.getTournamentId())
                .setTournamentBadge(badgeStr)
                .setDesc(levelModel.desc).buid();
        response.setSuccess(true);
        response.setDataAsJSON(claimResponse);
        socialController.getNodeToProxy().addServerMessage(response, this);

        tournamentModels.remove(tournament.getTournamentId());
        DBController.getInstance().removeUserTournamentModel(this.getId(),tournament.getTournamentId());
        CacheController.getInstance().removeUserTournamentModel(this.id, tournament.getTournamentId());

        if (levelModel.level == tournament.getMaxLevelId()) {
            incrementWonTournament(tournament.getTournamentId());
        }

        return success;
    }

    public Node getSocialNode() {
        return socialNode;
    }

    public void setSocialNode(Node socialNode) {
        this.socialNode = socialNode;
    }

    public GameAction getLastGameAction() {
        return lastGameAction;
    }

    public void setLastGameAction(GameAction lastGameAction) {
        this.lastGameAction = lastGameAction;
    }

    public int getChatMessageCount() {
        return chatMessageCount;
    }

    public void setChatMessageCount(int chatMessageCount) {
        this.chatMessageCount = chatMessageCount;
    }

    public void updateBasicUser(UserModel model){
        if(this.userModel != null){
            this.userModel.money = model.money;
            this.userModel.vip = model.vip;
            this.userModel.tickets = model.tickets;
        }
    }

    public boolean isConnected() {
        return connected;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }
}
