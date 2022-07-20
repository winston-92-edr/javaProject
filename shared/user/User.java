package com.mynet.shared.user;

import com.mynet.bonusservice.model.UserBonusInfo;
import com.mynet.gameserver.okey.Level;
import com.mynet.shared.analytics.AnalyticsLogController;
import com.mynet.shared.config.ServerGlobalVariables;
import com.mynet.shared.logs.RabbitMQLogController;
import com.mynet.shared.logs.WinnerDbLogType;
import com.mynet.shared.logs.WinnerLogDbData;
import com.mynet.shared.model.*;
import com.mynet.shared.resource.*;
import com.mynet.shared.resource.db.*;
import com.mynet.shared.resource.db.work.*;
import com.mynet.shared.types.DataSourceType;
import com.mynet.socialserver.model.FriendRequestModel;
import com.mynet.socialserver.model.UserTournamentStats;
import org.redisson.api.RMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.mynet.proxyserver.user.UserModel;
import com.mynet.shared.logs.TournamentEventLog;


import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

public abstract class User {
    private static Logger logger = LoggerFactory.getLogger(User.class.getName());

    protected String id;
    protected Hashtable<Integer, UserTournamentModel> tournamentModels;
    protected CacheController cacheController;
    protected ReentrantLock processLock;
    protected String tournamentBadgeStr;
    protected UserModel userModel;


    protected String ip;
    protected String platform;

    protected TournamentEventLog tournamentEventLog;

    protected Set<String> chatList;
    protected HashMap<String, FriendRequestModel> friendRequests = new HashMap<>();
    protected HashMap<String, Integer> onlineFriends = new HashMap<>();
    protected boolean onlineFriendsSet = false;
    protected int friendsCount = 0;
    protected HashSet<String> gifts = new HashSet<>();
    private HashMap<Integer, UserBonusInfo> bonusMap;

    public User(UserModel model) {
        this.cacheController = CacheController.getInstance();
        processLock = new ReentrantLock();
        tournamentModels = new Hashtable<>();

        this.userModel = model;
        this.id = model.fuid;

        this.platform = cacheController.getUserLastPlatform(this.id);
        this.ip = cacheController.getUserLastIp(id);
        userModel.proxyID = cacheController.getUserProxyNode(this.id);
        userModel.gameID = cacheController.getUserGameNode(this.id);
    }

    public UserTournamentModel getTournament(int id) {
        UserTournamentModel tm = null;
        try {
            processLock.lock();
            CacheController cacheController = CacheController.getInstance();
            UserTournamentModel model = cacheController.getUserTournamentModel(this.id, id);

            TournamentModel tournamentModel = TournamentLevelController.getInstance().getTournament(id);

            if(model != null) {
                if (model.level > tournamentModel.getLevels().size()) {
                    model.level = tournamentModel.getLevels().size();

                    cacheController.updateUserTournament(this.id, id, model);
                }
            }

            if (model!=null && (!tournamentModel.isActive() || (tournamentModel.getStartDate() > model.endDate))) {
                model.state = UserTournamentState.FAILED;
                model.remainingTryCount = 0;
                model.lostGames = tournamentModel.getFailPoint();
                model.expired = true;
                CacheController.getInstance().updateUserTournament(getId(),tournamentModel.getTournamentId(),model);
            }

            if(model == null){
                model = DBController.getInstance().getUserTournamentModel(this.id, id);
                if(model != null){
                    cacheController.updateUserTournament(this.id, id, model);
                }
            }

            if(model != null){
                tm = model;
                tm.expired = false;
                tournamentModels.put(id, tm);
            }

        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        } finally {
            processLock.unlock();
        }
        return tm;
    }

    public boolean updateMoneyAndWriteLog(String from, long game_id, long amount, boolean increment, long log_time) {

        boolean isSuccess = this.updateUserMoney(amount, false, increment);

        if (isSuccess) {
            if(!increment) amount *= -1;
            writeUserMoneyLog(from, game_id, amount, log_time);

            AnalyticsLogController.getInstance().sendGameEconomyLog(id, getAnalyticsDeviceId(), getAnalyticsSessionId(),null, getUserModel().money, amount, "chip", from, platform, getApplicationVersion());
        }

        return isSuccess;
    }

    public void writeUserMoneyLog(String from, long game_id, long amount, long log_time) {
        try {
            if (!this.getId().equals("-1")) {
                String parameters = this.getId() + "," + amount + "," + log_time + "," + from + "," + game_id + "," + this.userModel.money + "," + getPlatform();
                RabbitMQLogController.getInstance().addWinnerLogDbLog(new WinnerLogDbData(WinnerDbLogType.UPDATE_USER_MONEY_LOG, parameters, DataSourceType.WINNER_LOGS));
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    public boolean updateUserMoney(long amount, boolean isCheck, boolean increment) {
        if (this.id.equals("-1")) {
            return false;
        }

        long money = increment ? amount : -1 * amount;

        if(!increment && amount > userModel.money){
            return false;
        }

        long updatedMoney = DBController.getInstance().updateUserMoney(this.id, money, isCheck);

        if(updatedMoney < -1){
            logger.error(String.format("[NEGATIVE MONEY][USER: %s][RESULT: %d]", userModel.fuid, updatedMoney));
        } else if(updatedMoney != -1){
            long expected = this.userModel.money + money;
            this.userModel.money = updatedMoney;
            if(expected != updatedMoney){
                logger.warn(String.format("[ERROR AT UPDATE MONEY][USER: %s][EXPECTED: %d][RESULT: %d]", userModel.fuid, expected, updatedMoney));
            }

            return true;
        }

        return false;
    }

    public void updateMoney(long money){
        this.userModel.money = money;
    }

    public void updateVip(boolean status){
        this.userModel.vip = status;
    }

    public void updateTicket(long ticket){
        this.userModel.tickets = ticket;
    }

    public String getPlatform() {
        return platform;
    }

    public boolean canAffordTickets(long amount) {
        return this.userModel.tickets - amount >= 0;
    }

    public void decreaseTickets(int value) {
        this.userModel.tickets -= value;
    }

    public void increaseTickets(int value) {
        this.userModel.tickets += value;
    }


    public void setTournament(int tournamentId, UserTournamentModel model) {
        tournamentModels.put(tournamentId, model);
        CacheController.getInstance().updateUserTournament(id, tournamentId, model);
        DBController.getInstance().updateUserTournament(id, tournamentId, model);
    }

    public void beginTournamentEvent(TournamentEventLog.Type type) {
        this.tournamentEventLog = new TournamentEventLog(Long.parseLong(this.getId()), type);
    }
    public void fillTournamentEvent(int tournamentId, boolean success, long gameId, int cost, long amount) {
        if (tournamentEventLog != null) {
            UserTournamentModel tournamentModel = getTournament(tournamentId);
            TournamentModel tournament = TournamentLevelController.getInstance().getTournament(tournamentId);

            tournamentEventLog.setPlatform(this.getPlatform());
            tournamentEventLog.setLevel(tournamentModel.level);
            tournamentEventLog.setTid(tournamentModel.tid);
            tournamentEventLog.setTournament_id(tournamentId);
            tournamentEventLog.setSuccess(success);
            tournamentEventLog.setGame_id(gameId);
            tournamentEventLog.setLosses(tournamentModel.lostGames);
            tournamentEventLog.setRem_try_count(tournamentModel.remainingTryCount);
            tournamentEventLog.setPlayer_count(tournament.getSideCount());
            tournamentEventLog.setCost(cost);
            tournamentEventLog.setAmount(amount);
        }
    }

    public void endTournamentEvent() {
        if (tournamentEventLog != null) {
            tournamentEventLog.setEnd_time(System.currentTimeMillis());
            RabbitMQLogController.getInstance().addTournamentEventLog(tournamentEventLog);
        }
        tournamentEventLog = null;
    }

    public void incrementTotalTournament(int tournamentId) {
        IncrementTotalTournament work = new IncrementTotalTournament(id,tournamentId);
        DatabaseWorker.getInstance().addWork(new DatabaseWork(work,null));
        CacheController.getInstance().incrementTotalTournament(id);
    }

    public void incrementWonTournament(int tournamentId) {
        IncrementWonTournament work = new IncrementWonTournament(id,tournamentId);
        DatabaseWorker.getInstance().addWork(new DatabaseWork(work,null));
        CacheController.getInstance().incrementWonTournament(id);
    }

    public void incrementWonTournamentGames(int tournamentId) {
        IncrementWonGameTournament work = new IncrementWonGameTournament(id,tournamentId);
        DatabaseWorker.getInstance().addWork(new DatabaseWork(work,null));
        CacheController.getInstance().incrementWonTournamentGames(this.id);
    }

    public void incrementTotalTournamentGames(int tournamentId) {
        IncrementTotalGameTournament work = new IncrementTotalGameTournament(id,tournamentId);
        DatabaseWorker.getInstance().addWork(new DatabaseWork(work,null));
        CacheController.getInstance().incrementTotalTournamentGames(id);
    }

    public void setMaxLevel(int tournamentId, int level){
        SetMaxLevel work = new SetMaxLevel(this.id, tournamentId, level);
        DatabaseWorker.getInstance().addWork(new DatabaseWork(work,null));

        UserTournamentStats stats = CacheController.getInstance().getUserTournamentStats(this.id);

        if(stats == null || stats.max_level < level){
            CacheController.getInstance().updateMaxLevel(this.id, level);
        }
    }

    public String getTournamentBadge() {
        if (tournamentBadgeStr == null || tournamentBadgeStr.length() == 0) {
            TournamentBadge badgeObj = CacheController.getInstance().getUserTournamentBadge(id);
            tournamentBadgeStr = badgeObj != null ? badgeObj.toString() : "0";
        }
        return tournamentBadgeStr;
    }

    public String getId() {
        return id;
    }

    public int getGameNodeId() {
        return userModel.gameID;
    }

    public void setGamesTotal(int value) {
        userModel.gamesTotal = value;
    }

    public int getGamesTotal() {
        return userModel.gamesTotal;
    }

    public void updateUserGames() {
        UpdateUserGames work =new UpdateUserGames(id,userModel.gamesWon,userModel.gamesLost,userModel.gamesTotal);
        DatabaseWorker.getInstance().addWork(new DatabaseWork(work,null));
    }

    public Map<Integer, Long> getNewUserGameTotalSteps() {
        String val = ServerGlobalVariables.getInstance().getString("newUserGameTotalSteps", "3:1000,8:2000,13:3000");
        Map<Integer, Long> totalSteps = new HashMap<>();
        String[] stepsAndAwardsList = val.split(",");

        for (String item : stepsAndAwardsList) {
            String[] itemArray = item.split(":");
            totalSteps.put(Integer.valueOf(itemArray[0]), Long.valueOf(itemArray[1]));
        }

        return totalSteps;
    }

    public int getNewUserGameTotalStepsLimit() {
        return ServerGlobalVariables.getInstance().getInt("newUserGameTotalStepsLimit", 13);
    }

    public void updateStepsAward() {
        long userGamesCount = this.getGamesTotal();

        Map<Integer, Long> allAwards = this.getNewUserGameTotalSteps();
        int step = 0;
        for (Map.Entry<Integer, Long> entry : allAwards.entrySet()) {
            if (entry.getKey() == userGamesCount) {
                step = entry.getKey();
            }
        }

        if (step != 0) CacheController.getInstance().setStepsAward(id, step, allAwards.get(step));
    }

    public NewUserStepDetails userStepCheck() {

        NewUserStepDetails stepDetailsResponse = new NewUserStepDetails();
        stepDetailsResponse.setStatus(false);
        stepDetailsResponse.setVisible(false);
        if (getGamesTotal() < getNewUserGameTotalStepsLimit()) stepDetailsResponse.setVisible(true);

        Map<Integer, Long> allAwards = getNewUserGameTotalSteps();
        long userAward = getClosedAward(allAwards, getGamesTotal(), getNewUserGameTotalStepsLimit());

        stepDetailsResponse.setAward(userAward);

        try {


            int step = 0;
            long currentAward = 0;
            RMap<String, Long> stepsAwards = CacheController.getInstance().getStepsAward(id);

            if (stepsAwards != null) {
                for (Map.Entry<Integer, Long> entry : allAwards.entrySet()) {

                    String field = UserModel.F_STEPS_AWARD + "_" + entry.getKey();

                    if (stepsAwards.get(field) != null) {
                        long award = stepsAwards.get(field);
                        if (step == 0 || entry.getKey() < step) {
                            step = entry.getKey();
                            currentAward = award;
                            stepDetailsResponse.setAwardLevel(step);
                            stepDetailsResponse.setStatus(true);
                            stepDetailsResponse.setVisible(true);
                        }
                    }
                }
            }

            currentAward = currentAward != 0 ? currentAward:userAward;
            stepDetailsResponse.setAward(currentAward);
            stepDetailsResponse.setStep(getGamesTotal());

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        return stepDetailsResponse;
    }

    public long getClosedAward(Map<Integer, Long> awards, int step, int limit) {

        Long award = awards.get(step);

        int counter = step;
        while (counter <= limit && award == null) {

            award = awards.get(counter);
            counter++;
        }

        award = award != null ? award : 0;

        return award;
    }

    public String getIp() {
        if(ip == null){
            ip = cacheController.getUserLastIp(id);
        }
        return ip;
    }

    public void writeUserTicketLog(String from, int levelId, long amount, long log_time) {
        try {
            if (!this.getId().equals("-1")) {
                String parameters = this.getId() + "," + amount + "," + log_time + "," + from + "," + levelId + "," + this.userModel.tickets + "," + this.platform;
                RabbitMQLogController.getInstance().addWinnerLogDbLog(new WinnerLogDbData(WinnerDbLogType.UPDATE_USER_TICKET_LOG, parameters, DataSourceType.WINNER_LOGS));

                AnalyticsLogController.getInstance().sendGameEconomyLog(id, getAnalyticsDeviceId(), getAnalyticsSessionId(), null, getUserModel().tickets, amount, "ticket", from, platform, getApplicationVersion());
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    public void resetGift() {
        if(userModel != null){
            userModel.currentGift = "-1";
        }
    }

    public String getUserGift(){
        if(userModel != null){
            return userModel.currentGift;
        }

        return "-1";
    }

    public void setUserGift(String giftId){
        userModel.currentGift = giftId;
    }

    public long getLastTimeGiftSent() {
        return userModel.lastTimeGiftSent;
    }

    public void setLastTimeGiftSent(long lastTimeGiftSent) {
        userModel.lastTimeGiftSent = lastTimeGiftSent;
    }

    public int getGiftCount() {
        return userModel.giftCount;
    }

    public void setGiftCount(int giftCount) {
        userModel.giftCount = giftCount;
    }

    public int getExperiencePoints() {
        if(userModel != null){
            return userModel.experience;
        }

        return 0;
    }

    public boolean setExperiencePoints(int points){
        if(userModel != null){
            userModel.experience = points;

            int oldLevel = getLevel();
            if (userModel.vip) {
                points = 2 * points;
            }

            userModel.experience += points;
            int newLevel = getLevel();

            if (newLevel > oldLevel) {
                return !platform.endsWith("android");
            }
        }

        return false;
    }

    public int getLevel() {
        return Level.GetCurrentLevel(userModel.experience);
    }

    public long getGamesPot() {
        if(userModel != null){
            return userModel.gamesPot;
        }

        return 0;
    }

    public void setGamesPot(long pot) {
        if(userModel != null){
            userModel.gamesPot = pot;
        }
    }

    public void setPotMax(long value) {
        if(userModel != null){
            if (value > userModel.potMax) {
                userModel.potMax = value;
            }
        }
    }

    public void setPlatform(String platform){
        this.platform = platform;
    }

    public long getPotMax(){
        if(userModel != null){
            return userModel.potMax;
        }

        return 0;
    }

    public int getGamesWon() {
        if(userModel == null) return 0;
        return userModel.gamesWon;
    }

    public void setGamesWon(int value) {
        if(userModel != null){
            userModel.gamesWon = value;
        }
    }

    public int getGamesLost() {
        if(userModel == null) return 0;
        return userModel.gamesLost;
    }

    public void setGamesLost(int value) {
        if(userModel != null){
            userModel.gamesLost = value;
        }
    }

    public String getFirstName(){
        if(userModel != null){
            return userModel.firstName;
        }

        return "";
    }

    public String getLastName(){
        if(userModel != null){
            return userModel.lastName;
        }

        return "";
    }

    public UserModel getUserModel() {
        return userModel;
    }

    public boolean containFriendRequest(String fuid) {
        return friendRequests.containsKey(fuid);
    }

    public void addFriendRequest(FriendRequestModel friendRequest) {
        friendRequests.put(friendRequest.getInvitedFuid(), friendRequest);
    }

    public void removeFriendRequest(String invitedFuid) {
        friendRequests.remove(invitedFuid);
    }

    public HashMap<String, FriendRequestModel> getFriendRequests() {
        return friendRequests;
    }

    public void setFriendRequests(HashMap<String, FriendRequestModel> friendRequests) {
        this.friendRequests = friendRequests;
    }

    public HashMap<String, Integer> getOnlineFriends(){
        return onlineFriends;
    }

    public void setOnlineFriends(HashMap<String, Integer> onlineFriends){
        this.onlineFriends = onlineFriends;
    }

    public List<String> getOnlineFriendsList(){
        return new ArrayList<>(onlineFriends.keySet());
    }

    public void addOnlineFriend(String id, int proxyId){
        onlineFriends.put(id, proxyId);
    }

    public void removeOnlineFriend(String id){
        onlineFriends.remove(id);
    }

    public boolean hasOnlineFriend(String id){
        return onlineFriends.containsKey(id);
    }

    public boolean isOnlineFriendsSet() {
        return onlineFriendsSet;
    }

    public void setOnlineFriendsSet() {
        this.onlineFriendsSet = true;
    }

    public boolean didChatBefore(String fuid) {
        return chatList.contains(fuid);
    }

    public void addChatUser(String fuid) {
        if(!chatList.contains(fuid)) {
            chatList.add(fuid);
            cacheController.addChatList(id, fuid);
        }
    }

    public void setChatList(Set<String> chatList) {
        this.chatList = chatList;
    }

    public void setFriendsCount(int friendsCount) {
        this.friendsCount = friendsCount;
    }

    public int getFriendsCount() {
        return friendsCount;
    }

    public HashSet<String> getGifts() {
        return gifts;
    }

    public void setGifts(HashSet<String> gifts) {
        this.gifts = gifts;
    }

    public UserBonusInfo getBonus(int id) {
        return bonusMap.get(id);
    }

    public void addBonus(int id, UserBonusInfo info) {
        bonusMap.put(id, info);
    }

    public void increaseBonusCount(int id){
        UserBonusInfo info = bonusMap.get(id);
        if(info != null) info.increaseCount();

    }

    public void setBonusMap(HashMap<Integer, UserBonusInfo> bonusMap) {
        this.bonusMap = bonusMap;
    }

    public void setAnalyticsDeviceId(String deviceId) { this.userModel.analyticsDeviceId = deviceId; }

    public void setAnalyticsSessionId(String sessionId) { this.userModel.analyticsSessionId = sessionId; }

    public String getAnalyticsDeviceId() { return this.userModel.analyticsDeviceId; }

    public String getAnalyticsSessionId() { return this.userModel.analyticsSessionId; }

    public String getApplicationVersion() { return userModel.applicationVersion; }

    public void setApplicationVersion(String applicationVersion) { this.userModel.applicationVersion = applicationVersion; }
}
