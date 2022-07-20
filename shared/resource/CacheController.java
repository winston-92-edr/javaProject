package com.mynet.shared.resource;

import com.mynet.chatserver.models.SetChatUsersModel;
import com.mynet.gameserver.model.HalfGameModel;
import com.mynet.gameserver.model.RemoveGameUserModel;
import com.mynet.proxyserver.model.RemovedUserModel;
import com.mynet.questservice.quests.models.QuestSeasonModel;
import com.mynet.shared.config.ServerGlobalVariables;
import com.mynet.shared.model.UserTournamentModel;
import com.mynet.shared.node.NodeData;
import com.mynet.shared.user.ProxyUser;
import com.mynet.socialserver.model.UserTournamentStats;
import org.redisson.Redisson;
import org.redisson.api.*;
import org.redisson.api.listener.MessageListener;
import org.redisson.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.mynet.proxyserver.user.UserModel;
import com.mynet.shared.model.TournamentBadge;
import com.mynet.shared.model.UserTournamentState;
import com.mynet.shared.model.UserTournamentStatsModel;
import com.mynet.shared.utils.Utils;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class CacheController {
    private static final Logger logger = LoggerFactory.getLogger(CacheController.class);

    private static CacheController INSTANCE;
    private RedissonClient redissonClient;

    private static final String USERS_PREFIX = "C_USERS:";
    private static final String ONLINE_USERS = "C_TOURNAMENT_ONLINE_USERS";
    private static final String USER_TOURNAMENT_PREFIX = "c_USER_TOUR:";
    private static final String USER_TOURNAMENT_BADGE_PREFIX = "c_USER_TOUR_BADGE:";
    private static final String USER_TOURNAMENT_STATS_PREFIX = "c_USER_TOUR_STATS:";
    private static final String ACTIVE_USER = "ACTIVE_USER:";
    private static final String USER_AWARDS = "USER_AWARDS:";
    private static final String TABLE_COUNTER = "TABLE_COUNTER";
    private static final String BLACK_LIST = "BLACK_LIST";
    private static final String SUSPECT_LIST = "SUSPECT_LIST";
    private static final String TOP_LIST = "TOP_LIST";
    private static final String NODE_USERS = "NODE_USERS:";
    //private static final String USER_MODEL_PREFIX = "USER_MODEL:";
    private static final String ROOM_USER_COUNTS = "ROOM_USER_COUNTS_TEST";
    private static final String HALF_GAME = "HALF_GAME:";
    private static final String TABLES_INFO = "TABLES_INFO";

    private static final String GAME_NODES = "GAME_NODES";
    private static final String TABLE_NODE = "TABLE_NODE";
    private static final String SOCIAL_NODES = "SOCIAL_NODES";
    private static final String MATCH_NODE = "MATCH_NODE";
    private static final String QUEST_NODE = "QUEST_NODE";
    private static final String BONUS_NODE = "BONUS_NODE";
    private static final String CHAT_NODE = "CHAT_NODE";

    //Events from PHP
    private static final String MONEY_UPDATE_EVENT = "MONEY_UPDATE";
    private static final String TICKET_UPDATE_EVENT = "TICKET_UPDATE";
    private static final String VIP_UPDATE_EVENT = "VIP_UPDATE";
    private static final String BAN_USER_EVENT = "BAN_USER";
    private static final String MUTE_USER_EVENT = "MUTE_USER";

    //Events from Servers
    private static final String GAME_NODE_ADD_EVENT = "GAME_NODE_ADD_EVENT";
    private static final String GAME_NODE_DELETE_EVENT = "GAME_NODE_DELETE_EVENT";
    private static final String TABLE_NODE_ADD_EVENT = "TABLE_NODE_ADD_EVENT";
    private static final String TABLE_NODE_DELETE_EVENT = "TABLE_NODE_DELETE_EVENT";
    private static final String SOCIAL_NODE_ADD_EVENT = "SOCIAL_NODE_ADD_EVENT";
    private static final String SOCIAL_NODE_DELETE_EVENT = "SOCIAL_NODE_DELETE_EVENT";
    private static final String MATCH_NODE_ADD_EVENT = "MATCH_NODE_ADD_EVENT";
    private static final String MATCH_NODE_DELETE_EVENT = "MATCH_NODE_DELETE_EVENT";
    private static final String TABLES_RESET_EVENT = "TABLES_RESET_EVENT";
    private static final String SEND_TABLE_REQUEST_EVENT = "SEND_TABLE_REQUEST_EVENT";
    private static final String QUEST_NODE_ADD_EVENT = "QUEST_NODE_ADD_EVENT";
    private static final String QUEST_NODE_DELETE_EVENT = "QUEST_NODE_DELETE_EVENT";
    private static final String BONUS_NODE_ADD_EVENT = "BONUS_NODE_ADD_EVENT";
    private static final String BONUS_NODE_DELETE_EVENT = "BONUS_NODE_DELETE_EVENT";
    private static final String CHAT_NODE_ADD_EVENT = "CHAT_NODE_ADD_EVENT";
    private static final String CHAT_NODE_DELETE_EVENT = "CHAT_NODE_DELETE_EVENT";

    private static final String NEW_SERVER_REMOVE_USER_EVENT = "NEW_SERVER_REMOVE_USER_EVENT";
    private static final String OLD_SERVER_REMOVE_USER_EVENT = "OLD_SERVER_REMOVE_USER_EVENT";
    private static final String PROXY_REMOVE_USER_EVENT = "PROXY_REMOVE_USER_EVENT";
    private static final String GAME_USER_REMOVE_EVENT = "GAME_USER_REMOVE_EVENT";
    private static final String SOCIAL_REMOVE_EVENT = "SOCIAL_USER_REMOVE_EVENT";

    //QUEST
    private static final String USER_EXTRA_XP = "PROD_USER_EXTRA_XP:";
    private static final String USER_DIFFERENT_DAYS_PLAY = "PROD_USER_DIFFERENT_DAYS_PLAY:";
    private static final String WINNING_STREAK = "PROD_WINNING_STREAK:";
    private static final String QUEST_SEASON = "PROD_QUEST_SEASON";
    private static final String USER_QUEST_SEASON = "PROD_USER_QUEST_SEASON:";
    private static final String USER_QUEST_UPDATE = "PROD_USER_QUEST_UPDATE:";
    private static final String USER_DIFFERENT_DAYS_LOGIN = "PROD_USER_DIFFERENT_DAYS_LOGIN:";
    private static final String SEASON_AWARD_NOTIFICATION = "PROD_SEASON_AWARD_NOTIFICATION";
    private static final String UPDATE_SEASON_EVENT = "PROD_UPDATE_SEASON_EVENT";

    //Friends list
    private static final String ONLINE_FRIENDS = "ONLINE_FRIENDS:";

    //Private chat
    private static final String CHAT_LIST = "CHAT_LIST:";

    private static final String FORCE_UPDATE = "FORCE_UPDATE";

    //Bonus service
    private static final String BONUS = "BONUS:";
    private static final String UPDATE_BONUSES_EVENT = "UPDATE_BONUSES_EVENT";

    //Notification Urls
    private static final String UPDATE_NOTIFICATION_URLS = "UPDATE_NOTIFICATION_URLS";

    private static final String SET_CHAT_USERS = "SET_CHAT_USERS";

    private static final String SYSTEM_NOTIFICATION_MESSAGE = "SYSTEM_NOTIFICATION_MESSAGE";

    private static final String UPDATE_ANALYTICS_SESSIONID = "UPDATE_ANALYTICS_SESSIONID";

    private static final String PRIVATE_CHAT_HISTORY = "PRIVATE_CHAT_HISTORY:";

    public static CacheController getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new CacheController();
        }
        return INSTANCE;
    }

    private CacheController() {
    }

    public static void init() {
        try {
            CacheController.getInstance().setClient();
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
    }

    private void setClient() {
        if (this.redissonClient == null) {
            try {
                File cFile = new File("redisson-config-file.json");
                Config config = Config.fromJSON(cFile);
                this.redissonClient = Redisson.create(config);
            } catch (Exception ex) {
                logger.error(ex.getMessage(), ex);
            }
        }
    }

    private RMap<String, Object> getTournamentMap(String userId, int tournamentId) {
        try {
            String key = USER_TOURNAMENT_PREFIX + userId + "_" + tournamentId;
            RMap<String, Object> map = redissonClient.getMap(key);
            if (map.isExists()) {
                return map;
            }

        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
        return null;
    }

    private RMap<String, Object> getUserMap(String fuid) {
        try {
            String key = USERS_PREFIX + fuid;
            RMap<String, Object> map = redissonClient.getMap(key);
            if (map.isExists()) {
                return map;
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }

        return null;
    }

    public UserModel getUserGameModel(String userId) {
        RMap<String, Object> userMap = getUserMap(userId);
        try {
            if (userMap != null) {
                UserModel user = UserModel.createEmpty();
                user.fuid = userId;
                user.socialID = userMap.get(UserModel.F_SOCIALID) == null ? -1 : Integer.parseInt(userMap.get(UserModel.F_SOCIALID).toString());
                user.gameID = userMap.get(UserModel.F_GAMEID) == null ? -1 : Integer.parseInt(userMap.get(UserModel.F_GAMEID).toString());
                user.tableID = userMap.get(UserModel.F_TABLEID) == null ? -1 : Integer.parseInt(userMap.get(UserModel.F_TABLEID).toString());
                user.roomID = userMap.get(UserModel.F_ROOMID) == null ? -1 : Integer.parseInt(userMap.get(UserModel.F_ROOMID).toString());
                user.proxyID = userMap.get(UserModel.F_PROXYID) == null ? -1 : Integer.parseInt(userMap.get(UserModel.F_PROXYID).toString());
                user.gameServerId = userMap.get(UserModel.F_GAMESERVERID) == null ? -1 : Integer.parseInt(userMap.get(UserModel.F_GAMESERVERID).toString());
                user.analyticsSessionId = userMap.get(UserModel.F_ANALYTICSSESSIONID) == null ? "" : userMap.get(UserModel.F_ANALYTICSSESSIONID).toString();
                user.analyticsDeviceId = userMap.get(UserModel.F_ANALYTICSDEVICEID) == null ? "" : userMap.get(UserModel.F_ANALYTICSDEVICEID).toString();
                user.applicationVersion = userMap.get(UserModel.F_APPVERSION) == null ? "" : userMap.get(UserModel.F_APPVERSION).toString();
                user.name = userMap.get(UserModel.F_NAME) == null ? "" : userMap.get(UserModel.F_NAME).toString();
                return user;
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        return null;
    }

    public UserTournamentModel getUserTournamentModel(String userId, int tournamentId) {

        RMap<String, Object> tournament = getTournamentMap(userId, tournamentId);
        try {
            if (tournament != null) {
                UserTournamentModel.Builder b = new UserTournamentModel.Builder(Integer.parseInt(tournament.get(UserTournamentModel.F_LEVEL).toString()));
                b.setLostGames(Integer.parseInt(tournament.get(UserTournamentModel.F_LOSTGAMES).toString()));
                b.setState(UserTournamentState.values()[Integer.parseInt(tournament.get(UserTournamentModel.F_STATE).toString())]);
                b.setAward(Integer.parseInt(tournament.get(UserTournamentModel.F_AWARD).toString()));
                b.setDesc(tournament.get(UserTournamentModel.F_DESC).toString());
                b.setTitle(tournament.get(UserTournamentModel.F_TITLE).toString());
                b.setLastLevel(Boolean.parseBoolean(tournament.get(UserTournamentModel.F_LAST_LEVEL).toString()));
                b.setClaimed(Boolean.parseBoolean(tournament.get(UserTournamentModel.F_CLAIMED).toString()));
                b.setFailPoint(Integer.parseInt(tournament.get(UserTournamentModel.F_FAIL_POINT).toString()));
                b.setRemainingTryCount(Integer.parseInt(tournament.get(UserTournamentModel.F_REMAINING_TRY_COUNT).toString()));
                b.setTournamentId(tournamentId);
                Object tidO = tournament.get(UserTournamentModel.F_TID);
                if (tidO != null) {
                    b.setTid(Long.parseLong(tidO.toString()));
                } else {
                    long tid = System.currentTimeMillis();
                    b.setTid(tid);
                    // set back to redis
                    tournament.putAsync(UserTournamentModel.F_TID, tid);
                }

                Object endDate = tournament.get(UserTournamentModel.F_END_DATE);
                if (endDate != null) b.setEndDate(Long.parseLong(endDate.toString()));
                else b.setEndDate(-1);
                return b.build();
            }

        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
        return null;
    }

    public void addUser(ProxyUser user, int lobbyId) {
        try {
            addUserModel(user, lobbyId);
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
    }

    private void addUserModel(ProxyUser user, int lobbyId) {
        try {
            String key = USERS_PREFIX + user.getId();
            RBatch batch = redissonClient.createBatch();
            batch.getMap(key).fastPutAsync(UserModel.F_ID, user.getId());
            batch.getMap(key).fastPutAsync(UserModel.F_ROOMID, user.getRoomId());
            batch.getMap(key).fastPutAsync(UserModel.F_TABLEID, user.getTableId());
            batch.getMap(key).fastPutAsync(UserModel.F_PROXYID, user.getProxyId());
            batch.getMap(key).fastPutAsync(UserModel.F_GAMEID, user.getGameId());
            batch.getMap(key).fastPutAsync(UserModel.F_SOCIALID, user.getSocialId());
            batch.getMap(key).fastPutAsync(UserModel.F_GAMESERVERID, lobbyId);
            batch.getMap(key).fastPutAsync(UserModel.F_ANALYTICSSESSIONID, user.getAnalyticsSessionId());
            batch.getMap(key).fastPutAsync(UserModel.F_ANALYTICSDEVICEID, user.getAnalyticsDeviceId());
            batch.getMap(key).fastPutAsync(UserModel.F_APPVERSION, user.getApplicationVersion());
            batch.getMap(key).fastPutAsync(UserModel.F_NAME, user.getFirstName());
            batch.executeAsync();

            batch.getMap(key).expireAsync(2, TimeUnit.DAYS);
            
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
    }

    public void makeUserOnline(String userId) {
        try {
            RSet<String> set = redissonClient.getSet(ONLINE_USERS);
            set.add(userId);
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
    }

    public void makeUserOffline(String userId) {
        try {
            RSet<String> set = redissonClient.getSet(ONLINE_USERS);
            set.remove(userId);
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
    }

    public int getUserProxyNode(String userID) {
        UserModel model = getUserGameModel(userID);
        if (model != null) {
            return model.proxyID;
        }
        return -1;
    }

    public void removeUserTableID(String userID) {
        try {
            setUserTableID(-1, userID);
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
    }

    public void setUserTableID(int tableID, String userID) {
        setNodeID(UserModel.F_TABLEID, tableID, userID);
    }

    public void setUserRoomID(int roomID, String userID) {
        setNodeID(UserModel.F_ROOMID, roomID, userID);
    }

    public void setProxyNode(String userID, int nodeID) {
        setNodeID(UserModel.F_PROXYID, nodeID, userID);
    }

    public void setSocialNode(String userID, int nodeID) {
        setNodeID(UserModel.F_SOCIALID, nodeID, userID);
    }

    public void setGameNode(String userID, int nodeID) {
        setNodeID(UserModel.F_GAMEID, nodeID, userID);
    }

    public void setTableId(String userID, int tableId) {
        setNodeID(UserModel.F_TABLEID, tableId, userID);
    }

    public String getUserLastIp(String userId) {
        try {
            String key = USERS_PREFIX + userId;
            RMap<String, Object> map = redissonClient.getMap(key);

            if (map.isExists()) {
                Object platform = map.get(UserModel.F_IP);
                if (platform != null) {
                    return platform.toString();
                }
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }

        return null;
    }

    public void setUserLastPlatform(String userId, String platform) {
        try {
            String key = USERS_PREFIX + userId;
            RMap<String, Object> map = redissonClient.getMap(key);

            if (map.isExists()) {
                map.fastPut(UserModel.F_PLATFORM, platform);
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
    }

    public String getUserLastPlatform(String userId) {
        try {
            String key = USERS_PREFIX + userId;
            RMap<String, Object> map = redissonClient.getMap(key);

            if (map.isExists()) {
                Object platform = map.get(UserModel.F_PLATFORM);
                if (platform != null) {
                    return platform.toString();
                }
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }

        return null;
    }

    public void resetUserGameAndTable(String userID) {
        try {
            String key = USERS_PREFIX + userID;
            RMap<String, Object> map = redissonClient.getMap(key);

            if (map.isExists()) {
                map.fastPutAsync(UserModel.F_TABLEID, -1);
                map.fastPutAsync(UserModel.F_GAMEID, -1);
            }

            String statsKey = USER_TOURNAMENT_STATS_PREFIX + userID;
            RMap<String, Object> statsMap = redissonClient.getMap(statsKey);

            if(statsMap.isExists()){
                statsMap.deleteAsync();
            }

        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
    }

    private void setNodeID(String nodeType, int nodeID, String userID) {
        try {
            String key = USERS_PREFIX + userID;
            RMap<String, Object> map = redissonClient.getMap(key);

            if (map.isExists()) {
                map.fastPutAsync(nodeType, nodeID);
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
    }


    public int getUserGameNode(String fuid) {
        UserModel userGameModel = getUserGameModel(fuid);
        return userGameModel == null ? -1 : userGameModel.gameID;
    }

    public TournamentBadge getUserTournamentBadge(String userId) {
        try {
            String key = USER_TOURNAMENT_BADGE_PREFIX + userId;
            RMapCache<String, Object> map = redissonClient.getMapCache(key);
            if (map.isExists()) {
                Object tournamentId = map.get(TournamentBadge.TOURNAMENT_ID);
                Object badgeId = map.get(TournamentBadge.BADGE_ID);
                if (tournamentId != null && badgeId != null) {
                    return new TournamentBadge(Integer.parseInt(tournamentId.toString()), Integer.parseInt(badgeId.toString()));
                }
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
        return null;
    }

    public void updateUserTournament(String userId, int tournamentId, UserTournamentModel model) {
        try {
            String key = USER_TOURNAMENT_PREFIX + userId + "_" + tournamentId;
            updateTournamentModel(key, model);
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
    }

    private void updateTournamentModel(String key, UserTournamentModel model) {
        Map<String, Object> map = new HashMap<>();
        map.put(UserTournamentModel.F_TID, model.tid);
        map.put(UserTournamentModel.F_LEVEL, model.level);
        map.put(UserTournamentModel.F_LOSTGAMES, model.lostGames);
        map.put(UserTournamentModel.F_TITLE, model.title);
        map.put(UserTournamentModel.F_DESC, model.desc);
        map.put(UserTournamentModel.F_LAST_LEVEL, model.isLastLevel);
        map.put(UserTournamentModel.F_FAIL_POINT, model.failPoint);
        map.put(UserTournamentModel.F_AWARD, model.award);
        map.put(UserTournamentModel.F_CLAIMED, model.claimed);
        map.put(UserTournamentModel.F_STATE, model.state.getValue());
        map.put(UserTournamentModel.F_REMAINING_TRY_COUNT, model.remainingTryCount);
        map.put(UserTournamentModel.F_TOURNAMENT_ID, model.tournamentId);
        map.put(UserTournamentModel.F_END_DATE, model.endDate);

        RMap<String, Object> rMap = redissonClient.getMap(key);
        rMap.putAll(map);


//        RBatch batch = redissonClient.createBatch();
//        batch.getMap(key).fastPutAsync(UserTournamentModel.F_TID, model.tid);
//        batch.getMap(key).fastPutAsync(UserTournamentModel.F_LEVEL, model.level);
//        batch.getMap(key).fastPutAsync(UserTournamentModel.F_LOSTGAMES, model.lostGames);
//        batch.getMap(key).fastPutAsync(UserTournamentModel.F_TITLE, model.title);
//        batch.getMap(key).fastPutAsync(UserTournamentModel.F_DESC, model.desc);
//        batch.getMap(key).fastPutAsync(UserTournamentModel.F_LAST_LEVEL, model.isLastLevel);
//        batch.getMap(key).fastPutAsync(UserTournamentModel.F_FAIL_POINT, model.failPoint);
//        batch.getMap(key).fastPutAsync(UserTournamentModel.F_AWARD, model.award);
//        batch.getMap(key).fastPutAsync(UserTournamentModel.F_CLAIMED, model.claimed);
//        batch.getMap(key).fastPutAsync(UserTournamentModel.F_STATE, model.state.getValue());
//        batch.getMap(key).fastPutAsync(UserTournamentModel.F_REMAINING_TRY_COUNT, model.remainingTryCount);
//        batch.getMap(key).fastPutAsync(UserTournamentModel.F_TOURNAMENT_ID, model.tournamentId);
//        batch.getMap(key).fastPutAsync(UserTournamentModel.F_END_DATE, model.endDate);
//        batch.execute();
    }

    public void removeUserTournamentModel(String userId, int tournamentId) {
        try {
            String key = USER_TOURNAMENT_PREFIX + userId + "_" + tournamentId;
            RMap<String, Object> map = redissonClient.getMap(key);
            if (map.isExists()) {
                map.delete();
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
    }

    public void incrementTotalTournament(String userId) {
        RMap<String, Object> tournamentStats = getTournamentStatsMap(userId);
        try {
            if (tournamentStats != null) {
                Object totalTournament = tournamentStats.get(UserTournamentStatsModel.TOTAL_TOURNAMENT);
                if (totalTournament != null) {
                    int oldValue = Integer.parseInt(totalTournament.toString());
                    tournamentStats.fastPutAsync(UserTournamentStatsModel.TOTAL_TOURNAMENT, oldValue + 1);
                    long now = new Date().getTime();
                    tournamentStats.fastPutAsync(UserTournamentStatsModel.UPDATE_DATE, now);
                }
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
    }

    private RMap<String, Object> getTournamentStatsMap(String userId) {
        try {
            String key = USER_TOURNAMENT_STATS_PREFIX + userId;
            RMap<String, Object> map = redissonClient.getMap(key);
            if (map.isExists()) {
                return map;
            }

        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
        return null;
    }

    public void setUserTournamentBadge(String userId, TournamentBadge badge) {
        try {
            String key = USER_TOURNAMENT_BADGE_PREFIX + userId;
            RBatch batch = redissonClient.createBatch();
            batch.getMapCache(key).fastPutAsync(TournamentBadge.TOURNAMENT_ID, badge.getTournamentId(), 3, TimeUnit.DAYS);
            batch.getMapCache(key).fastPutAsync(TournamentBadge.BADGE_ID, badge.getBadgeId(), 3, TimeUnit.DAYS);
            batch.executeAsync();
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
    }

    public void incrementWonTournament(String userId) {
        RMap<String, Object> tournamentStats = getTournamentStatsMap(userId);
        try {
            if (tournamentStats != null) {
                Object wonTournament = tournamentStats.get(UserTournamentStatsModel.WON_TOURNAMENT);
                if (wonTournament != null) {
                    int oldValue = Integer.parseInt(wonTournament.toString());
                    tournamentStats.fastPutAsync(UserTournamentStatsModel.WON_TOURNAMENT, oldValue + 1);
                    long now = new Date().getTime();
                    tournamentStats.fastPutAsync(UserTournamentStatsModel.UPDATE_DATE, now);
                }
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
    }

    public int incAndGetTableCounter() {
        RAtomicLong counter = redissonClient.getAtomicLong(TABLE_COUNTER);
        if (counter.isExists()) {
            long tableID = counter.incrementAndGet() % 100000;
            return (int) tableID;
        } else {
            counter.set(1);
            return 1;
        }
    }


    public void incrementWonTournamentGames(String userId) {
        RMap<String, Object> tournamentStats = getTournamentStatsMap(userId);
        try {
            if (tournamentStats != null) {
                Object wonGames = tournamentStats.get(UserTournamentStatsModel.WON_GAMES);
                if (wonGames != null) {
                    int oldValue = Integer.parseInt(wonGames.toString());
                    long now = new Date().getTime();
                    tournamentStats.fastPutAsync(UserTournamentStatsModel.UPDATE_DATE, now);
                    tournamentStats.fastPut(UserTournamentStatsModel.WON_GAMES, oldValue + 1);
                }
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
    }

    public void incrementTotalTournamentGames(String userId) {
        RMap<String, Object> tournamentStats = getTournamentStatsMap(userId);
        try {
            if (tournamentStats != null) {
                Object totalGames = tournamentStats.get(UserTournamentStatsModel.TOTAL_GAMES);
                if (totalGames != null) {
                    int oldValue = Integer.parseInt(totalGames.toString());
                    tournamentStats.fastPutAsync(UserTournamentStatsModel.TOTAL_GAMES, oldValue + 1);
                    long now = new Date().getTime();
                    tournamentStats.fastPutAsync(UserTournamentStatsModel.UPDATE_DATE, now);
                }
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
    }

    public void setUserActive(String userId) {
        try {
            String key = ACTIVE_USER + userId;
            RBucket<String> bucket = redissonClient.getBucket(key);
            long duration = ((Utils.getMidnight() - System.currentTimeMillis()) / 1000) + (60 * 60 * 24);
            bucket.setAsync("1", duration, TimeUnit.SECONDS);
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
    }

    public int setStepsAward(String userId, int step, long award) {
        String key = USER_AWARDS + userId;
        RMap<String, Object> awards = redissonClient.getMap(key);
        try {
            String field = UserModel.F_STEPS_AWARD + "_" + step;
            awards.fastPut(field, award);
        } catch (Exception ex) {
        }

        return 0;
    }

    public RMap<String, Long> getStepsAward(String userId) {
        String key = USER_AWARDS + userId;
        RMap<String, Long> awards = redissonClient.getMap(key);
        try {
            if (awards.isExists()) {
                return awards;
            }
        } catch (Exception ex) {

        }

        return null;
    }


    public boolean isBlackListed(String userId) {
        try {
            RSet<String> rSet = redissonClient.getSet(BLACK_LIST);
            Set<String> strings = rSet.readAll();
            return strings.contains(userId);
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }

        return false;
    }

    public HashSet<String> getBlackList() {
        HashSet<String> set = new HashSet<>();
        try {
            RSet<String> rSet = redissonClient.getSet(BLACK_LIST);
            if (rSet.isExists()) {
                set.addAll(rSet.readAll());
                return set;
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }

        return set;
    }

    public void addToBlackList(String userId) {
        try {
            RSet<String> rSet = redissonClient.getSet(BLACK_LIST);
            rSet.add(userId);
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
    }

    public void addToSuspectList(String userId) {
        try {
            RSet<String> rSet = redissonClient.getSet(SUSPECT_LIST);
            rSet.add(userId);
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
    }

    public void flushOnlineUsers() {
        try {
            RSet<String> set = redissonClient.getSet(ONLINE_USERS);
            set.clear();
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
    }

    public void incrementUserTopListScore(String userId, long value) {
        try {
            long startDate = ServerGlobalVariables.getInstance().getLong("topListStartDate", -1);
            long endDate = ServerGlobalVariables.getInstance().getLong("topListEndDate", -1);
            long now = System.currentTimeMillis();

            if ((startDate != -1) && (endDate != -1) && (startDate <= now) && (now < endDate)) {
                String key = TOP_LIST;

                RScoredSortedSet topList = redissonClient.getScoredSortedSet(key);
                topList.addScore(userId, value);
            }
        } catch (Exception e) {
            logger.info(e.getMessage());
        }

    }

    public int incrementAndGetWinningStreak(String fuid, int season, long ttl) {
        String key = WINNING_STREAK + fuid + ":" + season;
        RBucket<Integer> bucket = redissonClient.getBucket(key);

        try {

            if (!bucket.isExists()) {
                bucket.set(1, ttl, TimeUnit.MILLISECONDS);
                return 1;
            } else {
                int streak = bucket.get() + 1;
                bucket.set(streak, ttl, TimeUnit.MILLISECONDS);
                return streak;
            }

        } catch (Exception ex) {
            logger.info(ex.getMessage());
        }

        return 0;
    }

    public void clearWinningStreak(String fuid, int season) {
        String key = WINNING_STREAK + fuid + ":" + season;
        RBucket<Integer> bucket = redissonClient.getBucket(key);
        bucket.delete();
    }

    public QuestSeasonModel getSeason() {
        try {
            RMap<String, String> map = redissonClient.getMap(QUEST_SEASON);
            if (map.isExists()) {
                String id = map.get("id");
                return new QuestSeasonModel(Integer.parseInt(id), map.get("start"), map.get("end"));
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
        return null;
    }

    public void setSeason(QuestSeasonModel model) {
        try {
            RMap<String, String> map = redissonClient.getMap(QUEST_SEASON);

            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            map.fastPut("id", model.getId() + "");
            map.fastPut("start", dateFormat.format(model.getStartDate()));
            map.fastPut("end", dateFormat.format(model.getEndDate()));
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
    }

    public Map<Integer, Integer> getUserSeason(String userId, int season) {
        Map<Integer, Integer> seasonMap = new HashMap<>();
        try {
            String key = USER_QUEST_SEASON + userId + ":" + season;
            RMap<Integer, Integer> map = redissonClient.getMap(key);
            if (map.isExists()) {
                seasonMap.putAll(map);
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
        return seasonMap;
    }

    public void deleteUserSeason(String userId, int season) {
        try {
            String key = USER_QUEST_SEASON + userId + ":" + season;
            RMap<Integer, Integer> map = redissonClient.getMap(key);
            if (map.isExists()) {
                map.delete();
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
    }

    public void setUserSeasonAward(String userId, int season, int awardId) {
        try {
            String key = USER_QUEST_SEASON + userId + ":" + season;
            RMap<Integer, Integer> map = redissonClient.getMap(key);
            map.fastPut(awardId, 0);
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
    }

    public void claimUserSeasonLevelAward(String userId, int season, int awardId) {
        try {
            String key = USER_QUEST_SEASON + userId + ":" + season;
            RMap<Integer, Integer> map = redissonClient.getMap(key);
            if (map.isExists()) {
                map.fastPut(awardId, 1);
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
    }

    public int getDifferentDaysPlay(String userId, int season) {
        String key = USER_DIFFERENT_DAYS_PLAY + userId + ":" + season;
        RBucket<Integer> bucket = redissonClient.getBucket(key);

        try {
            if (bucket.isExists()) return bucket.get();
            else return -1;

        } catch (Exception ex) {
            logger.info(ex.getMessage());
        }

        return -1;
    }

    public void setUserDifferentDaysPlay(String userId, int day, int season, long ttl) {
        String key = USER_DIFFERENT_DAYS_PLAY + userId + ":" + season;
        RBucket<Integer> bucket = redissonClient.getBucket(key);

        try {
            bucket.set(day, ttl, TimeUnit.MILLISECONDS);
        } catch (Exception ex) {
            logger.info(ex.getMessage());
        }
    }

    public int getDifferentDaysLogin(String userId, int season) {
        String key = USER_DIFFERENT_DAYS_LOGIN + userId + ":" + season;
        RBucket<Integer> bucket = redissonClient.getBucket(key);

        try {
            if (bucket.isExists()) return bucket.get();
            else return -1;

        } catch (Exception ex) {
            logger.info(ex.getMessage());
        }

        return -1;
    }

    public void setUserDifferentDaysLogin(String userId, int day, int season, long ttl) {
        String key = USER_DIFFERENT_DAYS_LOGIN + userId + ":" + season;
        RBucket<Integer> bucket = redissonClient.getBucket(key);

        try {
            bucket.setAsync(day, ttl, TimeUnit.MILLISECONDS);
        } catch (Exception ex) {
            logger.info(ex.getMessage());
        }
    }

    public int getWinningStreak(String fuid, int season) {
        String key = WINNING_STREAK + fuid + ":" + season;
        RBucket<Integer> bucket = redissonClient.getBucket(key);

        try {

            if (!bucket.isExists()) {
                return 0;
            } else {
                int streak = bucket.get();
                return streak;
            }

        } catch (Exception ex) {
            logger.info(ex.getMessage());
        }

        return 0;
    }

    public void setWinningStreak(String fuid, int season, long ttl, int streak) {
        String key = WINNING_STREAK + fuid + ":" + season;
        RBucket<Integer> bucket = redissonClient.getBucket(key);

        try {
            bucket.setAsync(streak, ttl, TimeUnit.MILLISECONDS);

        } catch (Exception ex) {
            logger.info(ex.getMessage());
        }
    }

    public int getExtraXpAmount(String userId, int seasonId) {
        String key = USER_EXTRA_XP + userId + ":" + seasonId;
        RMap<String, Object> map = redissonClient.getMap(key);

        try {
            if (map.isExists()) return (int) map.get("amount");
            else return 0;

        } catch (Exception ex) {
            logger.info(ex.getMessage());
        }

        return 0;
    }

    public void setExtraXpAmount(String userId, int seasonId, int amount) {
        String key = USER_EXTRA_XP + userId + ":" + seasonId;
        RMap<String, Object> map = redissonClient.getMap(key);

        try {
            map.fastPut("amount", amount);

        } catch (Exception ex) {
            logger.info(ex.getMessage());
        }

    }

    public int getExtraXpAward(String userId, int seasonId) {
        String key = USER_EXTRA_XP + userId + ":" + seasonId;
        RMap<String, Object> map = redissonClient.getMap(key);

        try {
            if (map.isExists()) return (int) map.get("award");
            else return 0;

        } catch (Exception ex) {
            logger.info(ex.getMessage());
        }

        return 0;
    }

    public void setExtraXpAward(String userId, int seasonId, int award) {
        String key = USER_EXTRA_XP + userId + ":" + seasonId;
        RMap<String, Object> map = redissonClient.getMap(key);

        try {
            map.fastPut("award", award);

        } catch (Exception ex) {
            logger.info(ex.getMessage());
        }

    }

    public boolean isUserQuestUpdate(String userId, String updateKey) {
        String key = USER_QUEST_UPDATE + updateKey + ":" + userId;
        RBucket<Integer> bucket = redissonClient.getBucket(key);

        try {
            if (bucket.isExists()) return true;
            else return false;

        } catch (Exception ex) {
            logger.info(ex.getMessage());
        }

        return false;
    }


    public void clearNodeUserCount(int nodeId) {
        String key = NODE_USERS + nodeId;

        try {
            RSet<Object> set = redissonClient.getSet(key);
            if (set.isExists()) {
                set.readAll();
            }
        } catch (Exception ex) {
            logger.info(ex.getMessage());
        }
    }

    public void clearNodeRoomUsersCount(int nodeId, int roomId) {
        String key = NODE_USERS + nodeId + ":" + roomId;

        try {
            RBucket<AtomicLong> bucket = redissonClient.getBucket(key);
            bucket.delete();
        } catch (Exception ex) {
            logger.info(ex.getMessage());
        }
    }

    public long getGameNodeUserCount(int nodeId) {
        String key = NODE_USERS + nodeId;

        try {
            RSet<Object> set = redissonClient.getSet(key);

            if (set.isExists()) {
                return set.size();
            }

        } catch (Exception ex) {
            logger.info(ex.getMessage());
        }

        return 0;
    }

    private void incrementCounter(String key) {
        RAtomicLong counter = redissonClient.getAtomicLong(key);
        if (counter.isExists()) {
            counter.incrementAndGet();
        } else {
            counter.set(1);
        }
    }

    private void decrementCounter(String key) {
        RAtomicLong counter = redissonClient.getAtomicLong(key);
        if (counter.isExists()) {
            long count = counter.decrementAndGet();
            if (count <= 0) {
                counter.delete();
            }
        } else {
            counter.set(0);
        }
    }

    public void incNodeUserCounter(int nodeId, String userId) {
        String key = NODE_USERS + nodeId;
        try {
            RSet<Object> set = redissonClient.getSet(key);
            set.add(userId);
        } catch (Exception ex) {
            logger.info(ex.getMessage());
        }
    }

    public void decNodeUserCounter(int nodeId, String userId) {
        String key = NODE_USERS + nodeId;
        try {
            RSet<Object> set = redissonClient.getSet(key);
            if (set.isExists()) {
                set.remove(userId);
            }
        } catch (Exception ex) {
            logger.info(ex.getMessage());
        }
    }

    public void incRoomUserCounter(int nodeId, int roomId) {
        String key = NODE_USERS + nodeId + ":" + roomId;
        incrementCounter(key);
    }

    public void decRoomUserCounter(int nodeId, int roomId) {
        String key = NODE_USERS + nodeId + ":" + roomId;
        decrementCounter(key);
    }

    public Object getUserModelKey(String key) {
        RBucket<Object> bucket = this.redissonClient.getBucket(key);

        Object value = null;

        if (bucket.isExists()) {
            value = bucket.get();
            setUserModelKey(key, value);
        }

        return value;
    }

    public void setUserModelKey(String key, Object value) {
        RBucket<Object> bucket = this.redissonClient.getBucket(key);
        bucket.setAsync(value, 1, TimeUnit.HOURS);
    }

    public String getRoomUserCounts(String groupId) {
        try {
            RBucket<String> bucket = redissonClient.getBucket(ROOM_USER_COUNTS + ":" + groupId);
            if (bucket.isExists()) {
                return bucket.get();
            }
        } catch (Exception ex) {
            logger.info(ex.getMessage());
        }
        return "10.0,9.0,8.0,7.0,6.0,5.0,4.0,3.0,2.0,1.0";
    }

    public void updateRoomUserCounts(String val, String groupId) {
        try {
            RBucket<String> bucket = redissonClient.getBucket(ROOM_USER_COUNTS + ":" + groupId);
            bucket.set(val);
        } catch (Exception ex) {
            logger.info(ex.getMessage());
        }
    }

    public int getTableId(String fuid) {
        try {
            String key = USERS_PREFIX + fuid;
            RMap<String, Object> map = redissonClient.getMap(key);

            if (map.isExists()) {
                return map.get(UserModel.F_TABLEID) == null ? -1 : Integer.parseInt(map.get(UserModel.F_TABLEID).toString());
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }

        return -1;
    }

    public int getRoomId(String fuid) {
        try {
            String key = USERS_PREFIX + fuid;
            RMap<String, Object> map = redissonClient.getMap(key);

            if (map.isExists()) {
                return map.get(UserModel.F_ROOMID) == null ? -1 : Integer.parseInt(map.get(UserModel.F_ROOMID).toString());
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }

        return -1;
    }

    public void listenMoneyUpdateEvents(MessageListener listener) {
        listenEvents(listener, MONEY_UPDATE_EVENT);
    }

    public void listenTicketUpdateEvents(MessageListener listener) {
        listenEvents(listener, TICKET_UPDATE_EVENT);
    }

    public void listenVipUpdateEvents(MessageListener listener) {
        listenEvents(listener, VIP_UPDATE_EVENT);
    }

    public void listenBanUpdateEvents(MessageListener listener) {
        listenEvents(listener, BAN_USER_EVENT);
    }

    public void listenMuteUpdateEvents(MessageListener listener) {
        listenEvents(listener, MUTE_USER_EVENT);
    }

    private void listenEvents(MessageListener listener, String event) {
        try {
            RTopic topic = redissonClient.getTopic(event);
            topic.addListener(String.class, listener);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    public void setAsAvailableGameNode(NodeData node, String groupId) {
        try {
            String key = GAME_NODES + ":" + groupId;
            RMap<Integer, NodeData> map = redissonClient.getMap(key);
            map.put(node.getId(), node);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    public void setSocialNode(NodeData node, String groupId) {
        try {
            String key = SOCIAL_NODES + ":" + groupId;
            RMap<Integer, NodeData> map = redissonClient.getMap(key);
            map.put(node.getId(), node);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    public void deleteAvailableGameNode(int nodeId, String groupId) {
        try {
            String key = GAME_NODES + ":" + groupId;
            RMap<Integer, NodeData> map = redissonClient.getMap(key);
            map.remove(nodeId);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    public void deleteSocialNode(int nodeId, String groupId) {
        try {
            String key = SOCIAL_NODES + ":" + groupId;
            RMap<Integer, NodeData> map = redissonClient.getMap(key);
            map.remove(nodeId);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }


    public void setTableNode(NodeData node, String groupId) {
        setNode(node, groupId, TABLE_NODE);
    }

    public void setBonusNode(NodeData node, String groupId) { setNode(node, groupId, BONUS_NODE); }

    public void setChatNode(NodeData node, String groupId) { setNode(node, groupId, CHAT_NODE); }

    public void setQuestNode(NodeData node, String groupId) {
        setNode(node, groupId, QUEST_NODE);
    }


    public void setMatchNode(NodeData node, String groupId) {
        setNode(node, groupId, MATCH_NODE);
    }

    public void deleteBonusNode(String groupId) { deleteNode(groupId, BONUS_NODE); }

    public void deleteChatNode(String groupId) { deleteNode(groupId, CHAT_NODE); }

    public void deleteQuestlNode(String groupId) {
        deleteNode(groupId, QUEST_NODE);
    }

    public void deleteTableNode(String groupId) {
        deleteNode(groupId, TABLE_NODE);
    }

    public void deleteMatchNode(String groupId) {
        deleteNode(groupId, MATCH_NODE);
    }

    public void publishGameNodeDeleteEvent(int nodeId) {
        publishNodeDeleteEvent(nodeId, GAME_NODE_DELETE_EVENT);
    }

    public void publishTableNodeDeleteEvent(int nodeId) {
        publishNodeDeleteEvent(nodeId, TABLE_NODE_DELETE_EVENT);
    }

    public void publishSocialNodeDeleteEvent(int nodeId) {
        publishNodeDeleteEvent(nodeId, SOCIAL_NODE_DELETE_EVENT);
    }

    public void publishBonusNodeDeleteEvent(int nodeId) { publishNodeDeleteEvent(nodeId, BONUS_NODE_DELETE_EVENT); }

    public void publishChatNodeDeleteEvent(int nodeId) { publishNodeDeleteEvent(nodeId, CHAT_NODE_DELETE_EVENT); }

    public void publishMatchNodeDeleteEvent(int nodeId) {
        publishNodeDeleteEvent(nodeId, MATCH_NODE_DELETE_EVENT);
    }

    public void publishQuestNodeDeleteEvent(int nodeId) { publishNodeDeleteEvent(nodeId, QUEST_NODE_DELETE_EVENT); }

    public void publishGameNodeAddEvent(NodeData node) { publishNodeAddEvent(node, GAME_NODE_ADD_EVENT); }

    public void publishTableNodeAddEvent(NodeData node) { publishNodeAddEvent(node, TABLE_NODE_ADD_EVENT); }

    public void publishSocialNodeAddEvent(NodeData node) { publishNodeAddEvent(node, SOCIAL_NODE_ADD_EVENT); }

    public void publishBonusNodeAddEvent(NodeData node) { publishNodeAddEvent(node, BONUS_NODE_ADD_EVENT); }

    public void publishChatNodeAddEvent(NodeData node) { publishNodeAddEvent(node, CHAT_NODE_ADD_EVENT); }

    public void publishMatchNodeAddEvent(NodeData node) { publishNodeAddEvent(node, MATCH_NODE_ADD_EVENT); }

    public void publishQuestNodeAddEvent(NodeData node) { publishNodeAddEvent(node, QUEST_NODE_ADD_EVENT); }

    public void publishTablesInfoForUserEvent(String userId) {
        try {
            RTopic topic = redissonClient.getTopic(TABLES_INFO);
            topic.publish(userId);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    public void listenTablesInfoForUserEvent(MessageListener<String> listener) {
        try {
            RTopic topic = redissonClient.getTopic(TABLES_INFO);
            topic.addListener(String.class, listener);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    public void listenGameNodeDeleteEvent(MessageListener<Integer> listener) {
        listenNodeDeleteEvent(listener, GAME_NODE_DELETE_EVENT);
    }

    public void listenTableNodeDeleteEvent(MessageListener<Integer> listener) {
        listenNodeDeleteEvent(listener, TABLE_NODE_DELETE_EVENT);
    }

    public void listenSocialNodeDeleteEvent(MessageListener<Integer> listener) {
        listenNodeDeleteEvent(listener, SOCIAL_NODE_DELETE_EVENT);
    }

    public void listenBonusNodeDeleteEvent(MessageListener<Integer> listener) {
        listenNodeDeleteEvent(listener, BONUS_NODE_DELETE_EVENT);
    }

    public void listenChatNodeDeleteEvent(MessageListener<Integer> listener) {
        listenNodeDeleteEvent(listener, CHAT_NODE_DELETE_EVENT);
    }

    public void listenMatchNodeDeleteEvent(MessageListener<Integer> listener) {
        listenNodeDeleteEvent(listener, MATCH_NODE_DELETE_EVENT);
    }

    public void listenGameNodeAddEvent(MessageListener<NodeData> listener) {
        listenNodeAddEvent(listener, GAME_NODE_ADD_EVENT);
    }

    public void listenTableNodeAddEvent(MessageListener<NodeData> listener) {
        listenNodeAddEvent(listener, TABLE_NODE_ADD_EVENT);
    }

    public void listenSocialNodeAddEvent(MessageListener<NodeData> listener) {
        listenNodeAddEvent(listener, SOCIAL_NODE_ADD_EVENT);
    }

    public void listenBonusNodeAddEvent(MessageListener<NodeData> listener) {
        listenNodeAddEvent(listener, BONUS_NODE_ADD_EVENT);
    }

    public void listenChatNodeAddEvent(MessageListener<NodeData> listener) {
        listenNodeAddEvent(listener, CHAT_NODE_ADD_EVENT);
    }

    public void listenMatchNodeAddEvent(MessageListener<NodeData> listener) {
        listenNodeAddEvent(listener, MATCH_NODE_ADD_EVENT);
    }

    public void listenQuestNodeAddEvent(MessageListener<NodeData> listener) {
        listenNodeAddEvent(listener, QUEST_NODE_ADD_EVENT);
    }

    public void listenQuestNodeDeleteEvent(MessageListener<Integer> listener) {
        listenNodeDeleteEvent(listener, QUEST_NODE_DELETE_EVENT);
    }

    public void publishNodeDeleteEvent(int nodeId, String prefix) {
        try {
            RTopic topic = redissonClient.getTopic(prefix);
            topic.publish(nodeId);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    public void publishTablesReset() {
        try {
            RTopic topic = redissonClient.getTopic(TABLES_RESET_EVENT);
            topic.publish("table");
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    public void listenTablesReset(MessageListener<String> listener) {
        try {
            RTopic topic = redissonClient.getTopic(TABLES_RESET_EVENT);
            topic.addListener(String.class, listener);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    private void listenNodeAddEvent(MessageListener<NodeData> listener, String prefix) {
        try {
            RTopic topic = redissonClient.getTopic(prefix);
            topic.addListener(NodeData.class, listener);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    private void listenNodeDeleteEvent(MessageListener<Integer> listener, String prefix) {
        try {
            RTopic topic = redissonClient.getTopic(prefix);
            topic.addListener(Integer.class, listener);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    public void publishNodeAddEvent(NodeData node, String prefix) {
        try {
            RTopic topic = redissonClient.getTopic(prefix);
            topic.publish(node);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    public void deleteNode(String groupId, String prefix) {
        try {
            String key = prefix + ":" + groupId;
            RBucket<NodeData> bucket = redissonClient.getBucket(key);
            bucket.delete();
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    public void setNode(NodeData node, String groupId, String prefix) {
        try {
            String key = prefix + ":" + groupId;
            RBucket<NodeData> bucket = redissonClient.getBucket(key);
            bucket.set(node);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    public ArrayList<NodeData> getGameNodes(String groupId) {
        ArrayList<NodeData> list = new ArrayList<>();
        try {
            String key = GAME_NODES + ":" + groupId;
            RMap<Object, NodeData> redissonClientMap = redissonClient.getMap(key);
            if (redissonClientMap != null) {
                list.addAll(redissonClientMap.values());
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);

        }
        return list;

    }

    public ArrayList<NodeData> getSocialNodes(String groupId) {
        ArrayList<NodeData> list = new ArrayList<>();
        try {
            String key = SOCIAL_NODES + ":" + groupId;
            RMap<Object, NodeData> redissonClientMap = redissonClient.getMap(key);
            if (redissonClientMap != null) {
                list.addAll(redissonClientMap.values());
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);

        }
        return list;

    }

    public NodeData getTableNode(String groupId) {
        return getNode(groupId, TABLE_NODE);
    }

    public NodeData getMatchNode(String groupId) {
        return getNode(groupId, MATCH_NODE);
    }

    public NodeData getQuestNode(String groupId) {
        return getNode(groupId, QUEST_NODE);
    }

    public NodeData getBonusNode(String groupId) {
        return getNode(groupId, BONUS_NODE);
    }

    public NodeData getChatNode(String groupId) {
        return getNode(groupId, CHAT_NODE);
    }

    private NodeData getNode(String groupId, String prefix) {
        try {
            String key = prefix + ":" + groupId;
            RBucket<NodeData> bucket = redissonClient.getBucket(key);
            if (bucket.isExists()) {
                return bucket.get();
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);

        }
        return null;
    }

    public void publishProxyRemoveUser(RemovedUserModel user) {
        try {
            RTopic topic = redissonClient.getTopic(PROXY_REMOVE_USER_EVENT);
            topic.publish(user);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    public void publishRemoveUser(String user) {
        try {
            RTopic topic = redissonClient.getTopic(NEW_SERVER_REMOVE_USER_EVENT);
            topic.publish(user);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    public void publishRemoveGameUser(RemoveGameUserModel model) {
        try {
            RTopic topic = redissonClient.getTopic(GAME_USER_REMOVE_EVENT);
            topic.publish(model);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    public void publishRemoveSocialUser(RemoveGameUserModel model) {
        try {
            RTopic topic = redissonClient.getTopic(SOCIAL_REMOVE_EVENT);
            topic.publish(model);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    public void publishFixTableEvent(int tableId) {
        try {
            RTopic topic = redissonClient.getTopic(SEND_TABLE_REQUEST_EVENT);
            topic.publish(tableId);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    public void listenRemoveGameUser(MessageListener<RemoveGameUserModel> listener) {
        try {
            RTopic topic = redissonClient.getTopic(GAME_USER_REMOVE_EVENT);
            topic.addListener(RemoveGameUserModel.class, listener);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    public void listenRemoveSocialUser(MessageListener<RemoveGameUserModel> listener) {
        try {
            RTopic topic = redissonClient.getTopic(SOCIAL_REMOVE_EVENT);
            topic.addListener(RemoveGameUserModel.class, listener);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    public void listenFixTableEvent(MessageListener<Integer> listener) {
        try {
            RTopic topic = redissonClient.getTopic(SEND_TABLE_REQUEST_EVENT);
            topic.addListener(Integer.class, listener);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    public void listenProxyRemoveUser(MessageListener<RemovedUserModel> listener) {
        try {
            RTopic topic = redissonClient.getTopic(PROXY_REMOVE_USER_EVENT);
            topic.addListener(RemovedUserModel.class, listener);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    public void listenOldServerRemoveUser(MessageListener<String> listener) {
        try {
            RTopic topic = redissonClient.getTopic(OLD_SERVER_REMOVE_USER_EVENT);
            topic.addListener(String.class, listener);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    public void listenUpdateSeasonEvent(MessageListener<String> listener) {
        try {
            RTopic topic = redissonClient.getTopic(UPDATE_SEASON_EVENT);
            topic.addListener(String.class, listener);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    public void saveGame(HalfGameModel game, int nodeId) {
        try {
            String key = HALF_GAME + nodeId;
            RList<HalfGameModel> list = redissonClient.getList(key);
            list.add(game);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    public List<HalfGameModel> getAndClearHalfGames(int nodeId) {
        try {
            String key = HALF_GAME + nodeId;
            RList<HalfGameModel> list = redissonClient.getList(key);
            if (list != null) {
                List<HalfGameModel> games = list.readAll();
                list.clear();
                return games;
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        return new ArrayList<>();
    }


    public Set<String> getChatList(String id) {
        String key = CHAT_LIST + id;
        RSet<String> set = redissonClient.getSet(key);

        if (set.isExists()) {
            Set<String> chatList = set.readAll();
            return chatList;
        } else return new HashSet<>();
    }

    public void deleteChatList(String id) {
        String key = CHAT_LIST + id;
        RSet<String> list = redissonClient.getSet(key);

        if (list.isExists()) {
            list.deleteAsync();
        }
    }

    public void addChatList(String id, String chatId) {
        String key = CHAT_LIST + id;
        RSet<String> list = redissonClient.getSet(key);
        list.addAsync(chatId);
        list.expire(6, TimeUnit.HOURS);
    }

    public void addUserSeasonAwardNotification(String userId) {
        String key = SEASON_AWARD_NOTIFICATION;
        RMap<String, String> map = redissonClient.getMap(key);
        long date = (System.currentTimeMillis() / 1000) + 24 * 60 * 60;

        try {
            map.fastPut(userId, String.valueOf(date));
        } catch (Exception ex) {
            logger.info(ex.getMessage());
        }
    }

    public void updateUserTournamentStats(String userId, UserTournamentStats stats) {
        try {
            String key = USER_TOURNAMENT_STATS_PREFIX + userId;
            updateTournamentStats(key, stats);
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
    }

    private void updateTournamentStats(String key, UserTournamentStats stats) {
        RBatch batch = redissonClient.createBatch();
        batch.getMap(key).fastPutAsync(UserTournamentStats.FUID, stats.fuid);
        batch.getMap(key).fastPutAsync(UserTournamentStats.TOTAL_TOURNAMENT, stats.total_tournament);
        batch.getMap(key).fastPutAsync(UserTournamentStats.WON_TOURNAMENT, stats.won_tournament);
        batch.getMap(key).fastPutAsync(UserTournamentStats.TOTAL_GAMES, stats.total_games);
        batch.getMap(key).fastPutAsync(UserTournamentStats.WON_GAMES, stats.won_games);
        batch.getMap(key).fastPutAsync(UserTournamentStats.MAX_LEVEL, stats.max_level);
        batch.getMap(key).fastPutAsync(UserTournamentStats.CREATE_DATE, stats.create_date);
        batch.getMap(key).fastPutAsync(UserTournamentStats.UPDATE_DATE, stats.update_date);
        batch.executeAsync();
    }

    public void updateMaxLevel(String userId, int level){
        RMap<String, Object> tournamentStats = getTournamentStatsMap(userId);
        try {
            if (tournamentStats != null) {
                Object totalTournament = tournamentStats.get(UserTournamentStatsModel.MAX_LEVEL);
                if (totalTournament != null) {
                    int oldValue = Integer.parseInt(totalTournament.toString());
                    tournamentStats.fastPutAsync(UserTournamentStatsModel.MAX_LEVEL, level);
                    long now = new Date().getTime();
                    tournamentStats.fastPutAsync(UserTournamentStatsModel.UPDATE_DATE, now);
                }
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
    }

    public UserTournamentStats getUserTournamentStats(String userId) {

        RMap<String, Object> tournamentStats = getTournamentStatsMap(userId);
        try {
            if (tournamentStats != null) {
                UserTournamentStats.Builder b = new UserTournamentStats.Builder(tournamentStats.get(UserTournamentStats.FUID).toString());
                b.setTotalTournament(Integer.parseInt(tournamentStats.get(UserTournamentStats.TOTAL_TOURNAMENT).toString()));
                b.setWonTournament(Integer.parseInt(tournamentStats.get(UserTournamentStats.WON_TOURNAMENT).toString()));
                b.setTotalGames(Integer.parseInt(tournamentStats.get(UserTournamentStats.TOTAL_GAMES).toString()));
                b.setWonGames(Integer.parseInt(tournamentStats.get(UserTournamentStats.WON_GAMES).toString()));
                b.setMaxLevel(Integer.parseInt(tournamentStats.get(UserTournamentStats.MAX_LEVEL).toString()));
                int gameWinningPercent = 0;
                if (tournamentStats.get(UserTournamentStats.TOTAL_GAMES) != null && tournamentStats.get(UserTournamentStats.WON_GAMES) != null && Integer.parseInt(tournamentStats.get(UserTournamentStats.WON_GAMES).toString()) > 0)
                    gameWinningPercent = Integer.parseInt(tournamentStats.get(UserTournamentStats.WON_GAMES).toString()) / Integer.parseInt(tournamentStats.get(UserTournamentStats.TOTAL_GAMES).toString());
                b.setGameWinningPercent(gameWinningPercent);
                return b.build();
            } else return null;

        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
        return null;
    }

    public void listenForceUpdate(MessageListener<String> listener){
        try {
            RTopic topic = redissonClient.getTopic(FORCE_UPDATE);
            topic.addListener(String.class, listener);
        }catch (Exception e){
            logger.error(e.getMessage(), e);
        }
    }

    public void listenUpdateNotificationUrls(MessageListener<String> listener){
        try {
            RTopic topic = redissonClient.getTopic(UPDATE_NOTIFICATION_URLS);
            topic.addListener(String.class, listener);
        }catch (Exception e){
            logger.error(e.getMessage(), e);
        }
    }

    public boolean getBonusAvailable(String userId, int bonusId){
        try {
            String key = BONUS + bonusId + ":" +userId;
            RBucket<Integer> bucket = redissonClient.getBucket(key);
            if(bucket.isExists()){
                return false;
            }else{
                return true;
            }
        }catch (Exception e){
            logger.error(e.getMessage(), e);
        }

        return false;
    }

    public void setBonusKey(String userId, int bonusId, int interval){
        try {
            String key = BONUS + bonusId + ":" +userId;
            RBucket<String> bucket = redissonClient.getBucket(key);
            bucket.setAsync("1", interval, TimeUnit.HOURS);
        }catch (Exception e){
            logger.error(e.getMessage(), e);
        }
    }
    public void listenUpdateBonusesEvent(MessageListener<String> listener){
        try {
            RTopic topic = redissonClient.getTopic(UPDATE_BONUSES_EVENT);
            topic.addListener(String.class, listener);
        }catch (Exception e){
            logger.error(e.getMessage(), e);
        }
    }

    public void publishUpdateMoney(String userId) {
        try {
            RTopic topic = redissonClient.getTopic(MONEY_UPDATE_EVENT);
            topic.publish(userId);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    public void publishUpdateTicket(String userId) {
        try {
            RTopic topic = redissonClient.getTopic(TICKET_UPDATE_EVENT);
            topic.publish(userId);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    public void listenSystemNotificationMessage(MessageListener<String> listener){
        try {
            RTopic topic = redissonClient.getTopic(SYSTEM_NOTIFICATION_MESSAGE);
            topic.addListener(String.class, listener);
        }catch (Exception e){
            logger.error(e.getMessage(), e);
        }
    }

    public void publishSetChatUsers(SetChatUsersModel users) {
        try {
            RTopic topic = redissonClient.getTopic(SET_CHAT_USERS);
            topic.publish(users);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    public void listenSetChatUsers(MessageListener<SetChatUsersModel> listener) {
        try {
            RTopic topic = redissonClient.getTopic(SET_CHAT_USERS);
            topic.addListener(SetChatUsersModel.class, listener);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    public void listenUpdateSessionId(MessageListener<String> listener) {
        try {
            RTopic topic = redissonClient.getTopic(UPDATE_ANALYTICS_SESSIONID);
            topic.addListener(String.class, listener);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    public void clearChatHistory(String userId) {
        try {
            String key = PRIVATE_CHAT_HISTORY + userId;
            RBucket<Integer> bucket = redissonClient.getBucket(key);
            if (bucket.isExists()) {
                bucket.delete();
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

//    public HashMap<String, Integer> getOnlineFriends(String id){
//        String key = ONLINE_FRIENDS + id;
//        RMap<String, Integer> map = redissonClient.getMap(key);
//
//        if(map.isExists()){
//            HashMap<String, Integer> onlineFriends = new HashMap<String, Integer>(map.readAllMap());
//            return onlineFriends;
//        }else return null;
//    }

//    public void setOnlineFriends(String id, HashMap<String, Integer> onlineFriends){
//        String key = ONLINE_FRIENDS + id;
//        RMap<String, Integer> map = redissonClient.getMap(key);
//        map.putAllAsync(onlineFriends);
//    }
//
//    public void deleteOnlineFriends(String id){
//        String key = ONLINE_FRIENDS + id;
//        RMap<String, Integer> map = redissonClient.getMap(key);
//        if(map.isExists())  map.delete();
//    }
//
//    public void addOnlineFriend(String id, String friendId, Integer proxyId){
//        String key = ONLINE_FRIENDS + id;
//        RMap<String, Integer> map = redissonClient.getMap(key);
//        map.putAsync(friendId, proxyId);
//    }
//
//    public void removeOnlineFriend(String id, String friendId){
//        String key = ONLINE_FRIENDS + id;
//        RMap<String, Integer> map = redissonClient.getMap(key);
//        if(map.isExists()) map.removeAsync(friendId);
//    }
}