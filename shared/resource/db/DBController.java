package com.mynet.shared.resource.db;

import com.mynet.bonusservice.model.BonusModel;
import com.mynet.bonusservice.model.UserBonusInfo;
import com.mynet.gameserver.model.GiftModel;
import com.mynet.gameserver.model.HalfGameModel;
import com.mynet.gameserver.room.RoomType;
import com.mynet.proxyserver.user.UserModel;
import com.mynet.questservice.QuestSettings;
import com.mynet.questservice.quests.models.QuestAwardModel;
import com.mynet.questservice.quests.models.QuestModel;
import com.mynet.questservice.quests.models.QuestUserSeasonModel;
import com.mynet.questservice.quests.models.UserQuestModel;
import com.mynet.questservice.quests.types.QuestType;
import com.mynet.shared.config.ServerConfiguration;
import com.mynet.shared.db.generated.enums.NodesType;
import com.mynet.shared.model.BasicUserModel;
import com.mynet.shared.model.TournamentLevel;
import com.mynet.shared.model.TournamentModel;
import com.mynet.shared.model.UserTournamentModel;
import com.mynet.shared.network.Utils;
import com.mynet.shared.node.NodeData;
import com.mynet.shared.types.ServerType;
import com.mynet.shared.types.SettingsTypes;
import com.mynet.socialserver.model.FriendRequestModel;
import com.mynet.socialserver.model.UserTournamentStats;
import com.zaxxer.hikari.HikariDataSource;
import org.jooq.*;
import org.jooq.impl.DSL;
import org.jooq.types.UByte;
import org.jooq.types.UInteger;
import org.jooq.types.ULong;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.naming.CannotProceedException;
import java.math.BigInteger;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static com.mynet.shared.db.generated.Tables.*;
import static com.mynet.shared.db.generated.Tables.USER_QUEST_SEASONS;
import static com.mynet.shared.db.generated.friends.tables.Friends.FRIENDS;
import static com.mynet.shared.db.generated.tables.Alltables.ALLTABLES;
import static com.mynet.shared.db.generated.tables.HalfGames.HALF_GAMES;
import static com.mynet.shared.db.generated.tables.Members.MEMBERS;
import static com.mynet.shared.db.generated.tables.OverallScores.OVERALL_SCORES;
import static com.mynet.shared.db.generated.tables.Tickets.TICKETS;
import static com.mynet.shared.db.generated.tables.Tournament.TOURNAMENT;
import static com.mynet.shared.db.generated.tables.TournamentHistory.TOURNAMENT_HISTORY;
import static com.mynet.shared.db.generated.tables.TournamentLevels.TOURNAMENT_LEVELS;
import static com.mynet.shared.db.generated.tables.ZombiUser.ZOMBI_USER;

public class DBController {
    private static final Logger logger = LoggerFactory.getLogger(DBController.class);

    private HikariDataSource getConnection() {
        return DataSource.getDataSource().get();
    }

    private HikariDataSource getFriendsConnection() {
        return DataSource.getFriendsDataSource().get();
    }

    public DBController() {
    }

    private static DBController INSTANCE = null;


    public static DBController getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new DBController();
        }

        return INSTANCE;
    }

    public UserModel getUser(String id) throws CannotProceedException {
        try (DSLContext db = DSL.using(getConnection(), SQLDialect.MYSQL)) {
            Record record = db.select(OVERALL_SCORES.FUID, OVERALL_SCORES.GAMES_WON, OVERALL_SCORES.GAMES_TOTAL,
                    OVERALL_SCORES.GAMES_POT, OVERALL_SCORES.POT_MAX, OVERALL_SCORES.GAMES_LOST, OVERALL_SCORES.MONEY, OVERALL_SCORES.ISVIP, OVERALL_SCORES.CURRENT_GIFT,
                    OVERALL_SCORES.IS_MOBILE, OVERALL_SCORES.BANNED, OVERALL_SCORES.MUTED, OVERALL_SCORES.NAME, OVERALL_SCORES.FRIENDS_COUNT, MEMBERS.GO_PROFILE, MEMBERS.FIRSTNAME, MEMBERS.LASTNAME, MEMBERS.JOIN_DATE, MEMBERS.PAID_STATUS, TICKETS.TICKET, MEMBERS.CLOSED_INVITE, MEMBERS.PRIVATE_CHAT)
                    .from(OVERALL_SCORES)
                    .leftJoin(MEMBERS)
                    .on(OVERALL_SCORES.FUID.eq(MEMBERS.FUID))
                    .leftJoin(TICKETS)
                    .on(OVERALL_SCORES.FUID.eq(TICKETS.FUID))
                    .where(OVERALL_SCORES.FUID.eq(ULong.valueOf(id)))
                    .fetchOne();
            return record.into(UserModel.class);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        throw new CannotProceedException("User CAN NOT FOUND!");
    }

    public String getMuteDate(String id) throws CannotProceedException {
        try (DSLContext db = DSL.using(getConnection(), SQLDialect.MYSQL)){
            Record record = db.select(OVERALL_SCORES.MUTED).from(OVERALL_SCORES).where(OVERALL_SCORES.FUID.eq(ULong.valueOf(id))).fetchOne();
            Timestamp muteDate = record.get(OVERALL_SCORES.MUTED);
            return muteDate == null ? null : muteDate.toString();
        }catch(Exception e){
            logger.error(e.getMessage(), e);
        }

        throw new CannotProceedException("User CAN NOT FOUND!");
    }

    public BasicUserModel getBasicUser(String id) {
        try (DSLContext db = DSL.using(getConnection(), SQLDialect.MYSQL)) {
            Record record = db.select(OVERALL_SCORES.FUID, OVERALL_SCORES.MONEY, OVERALL_SCORES.ISVIP, OVERALL_SCORES.NAME)
                    .from(OVERALL_SCORES)
                    .where(OVERALL_SCORES.FUID.eq(ULong.valueOf(id)))
                    .fetchOne();

            String name = record.get(OVERALL_SCORES.NAME);
            long money = record.get(OVERALL_SCORES.MONEY);
            boolean isVip = record.get(OVERALL_SCORES.ISVIP) == 1;

            return new BasicUserModel(id, name, "android", money, isVip);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        return null;
    }

    public NodeData getMyNodeInfo(String host, int port) {
        try (DSLContext db = DSL.using(getConnection(), SQLDialect.MYSQL)) {
            Record record = db.select().from(NODES)
                    .where(NODES.IS_ACTIVE.eq((byte) 1)
                            .and(NODES.PORT.eq(port))
                            .and(NODES.HOST.eq(host)))
                    .limit(1)
                    .fetchOne();
            if (record != null) {
                return record.into(NodeData.class);
            }

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    public NodeData createNodeInfo(String host, int port, NodesType type, String groupId) {
        try (DSLContext db = DSL.using(getConnection(), SQLDialect.MYSQL)) {
            db.insertInto(NODES)
                    .columns(NODES.TYPE, NODES.HOST, NODES.PORT, NODES.IS_ACTIVE,NODES.GROUPID)
                    .values(type, host, port, (byte) 1, groupId)
                    .execute();
            BigInteger id = db.lastID();
            if (id != null) {
                return new NodeData.Builder()
                        .setId(id.intValue())
                        .setHost(host)
                        .setPort(port)
                        .setType(type.name())
                        .setActive(true)
                        .build();
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    public List<TournamentModel> getTournaments() {
        try (DSLContext db = DSL.using(getConnection(), SQLDialect.MYSQL)) {
            Result<Record> records = db.select()
                    .from(TOURNAMENT)
                    .orderBy(TOURNAMENT.TOURNAMENTID)
                    .fetch();
            if (records != null) {
                return records.into(TournamentModel.class);
            }

        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }

        return new ArrayList<>();
    }

    public List<TournamentLevel> getTournamentLevels() {
        try (DSLContext db = DSL.using(getConnection(), SQLDialect.MYSQL)) {
            Result<Record> records = db.select()
                    .from(TOURNAMENT_LEVELS)
                    .orderBy(TOURNAMENT_LEVELS.POSITION.asc())
                    .fetch();
            if (records != null) {
                return records.into(TournamentLevel.class);
            }

        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }

        return new ArrayList<>();
    }

    public List<TournamentLevel> getTournamentLevelsById(int tid) {
        try (DSLContext db = DSL.using(getConnection(), SQLDialect.MYSQL)) {
            Result<Record> records = db.select()
                    .from(TOURNAMENT_LEVELS)
                    .where(TOURNAMENT_LEVELS.TOURNAMENTID.eq(tid))
                    .fetch();
            if (records != null) {
                return records.into(TournamentLevel.class);
            }

        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }

        return new ArrayList<>();
    }


    public boolean updateUserTickets(String userId, int cost, boolean isAdd) {
        try (DSLContext db = DSL.using(getConnection(), SQLDialect.MYSQL)) {
            int result = db.update(TICKETS)
                    .set(TICKETS.TICKET, isAdd ? TICKETS.TICKET.add(cost) : TICKETS.TICKET.sub(cost))
                    .where(TICKETS.FUID.eq(ULong.valueOf(userId)))
                    .execute();
            return result == 1;
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
        return false;
    }

    public void incrementTotalTournament(String userId, int tournamentId) {
        try (DSLContext db = DSL.using(getConnection(), SQLDialect.MYSQL)) {
            db.update(TOURNAMENT_HISTORY)
                    .set(TOURNAMENT_HISTORY.TOTAL_TOURNAMENT, TOURNAMENT_HISTORY.TOTAL_TOURNAMENT.add(1))
                    .set(TOURNAMENT_HISTORY.UPDATE_DATE, DSL.currentTimestamp())
                    .where(TOURNAMENT_HISTORY.FUID.eq(Long.parseLong(userId)))
                    .and(TOURNAMENT_HISTORY.TOURNAMENT_ID.eq(tournamentId))
                    .executeAsync();
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
    }

    public void incrementWonTournament(String userId, int tournamentId) {
        try (DSLContext db = DSL.using(getConnection(), SQLDialect.MYSQL)) {
            db.update(TOURNAMENT_HISTORY)
                    .set(TOURNAMENT_HISTORY.WON_TOURNAMENT, TOURNAMENT_HISTORY.WON_TOURNAMENT.add(1))
                    .set(TOURNAMENT_HISTORY.UPDATE_DATE, DSL.currentTimestamp())
                    .where(TOURNAMENT_HISTORY.FUID.eq(Long.parseLong(userId)))
                    .and(TOURNAMENT_HISTORY.TOURNAMENT_ID.eq(tournamentId))
                    .executeAsync();
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
    }

    public void incrementWonGameTournament(String userId, int tournamentId) {
        try (DSLContext db = DSL.using(getConnection(), SQLDialect.MYSQL)) {
            db.update(TOURNAMENT_HISTORY)
                    .set(TOURNAMENT_HISTORY.WON_TOURNAMENT, TOURNAMENT_HISTORY.WON_GAME.add(1))
                    .set(TOURNAMENT_HISTORY.UPDATE_DATE, DSL.currentTimestamp())
                    .where(TOURNAMENT_HISTORY.FUID.eq(Long.parseLong(userId)))
                    .and(TOURNAMENT_HISTORY.TOURNAMENT_ID.eq(tournamentId))
                    .executeAsync();
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
    }

    public void incrementTotalGameTournament(String userId, int tournamentId) {
        try (DSLContext db = DSL.using(getConnection(), SQLDialect.MYSQL)) {
            db.update(TOURNAMENT_HISTORY)
                    .set(TOURNAMENT_HISTORY.WON_TOURNAMENT, TOURNAMENT_HISTORY.TOTAL_GAME.add(1))
                    .set(TOURNAMENT_HISTORY.UPDATE_DATE, DSL.currentTimestamp())
                    .where(TOURNAMENT_HISTORY.FUID.eq(Long.parseLong(userId)))
                    .and(TOURNAMENT_HISTORY.TOURNAMENT_ID.eq(tournamentId))
                    .executeAsync();
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
    }

    public long updateUserMoney(String id, long money, boolean isCheck) {
        Long userId = Long.parseLong(id);
        Byte check = isCheck ? (byte) 1 : (byte) 0;
        try (DSLContext db = DSL.using(getConnection(), SQLDialect.MYSQL)) {

            int success = db.update(OVERALL_SCORES)
                    .set(OVERALL_SCORES.MONEY, OVERALL_SCORES.MONEY.add(money))
                    .where(OVERALL_SCORES.FUID.eq(ULong.valueOf(id)))
                    .execute();

            if (success == 1) {
                Record1<Long> fetchOne = db.select(OVERALL_SCORES.MONEY).from(OVERALL_SCORES).where(OVERALL_SCORES.FUID.eq(ULong.valueOf(id))).fetchOne();
                return fetchOne.value1();
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }

        return -1;
    }

    public void updateUserGames(String id, int wonGames, int lostGames, int totalGames) {
        try (DSLContext db = DSL.using(getConnection(), SQLDialect.MYSQL)) {
            db.update(OVERALL_SCORES)
                    .set(OVERALL_SCORES.GAMES_WON, UInteger.valueOf(wonGames))
                    .set(OVERALL_SCORES.GAMES_LOST, UInteger.valueOf(lostGames))
                    .set(OVERALL_SCORES.GAMES_TOTAL, UInteger.valueOf(totalGames))
                    .where(OVERALL_SCORES.FUID.eq(ULong.valueOf(id)))
                    .execute();
        }
    }

    public int getUserTickets(String userId) {
        try (DSLContext db = DSL.using(getConnection(), SQLDialect.MYSQL)) {
            Record1<Integer> record = db.select(TICKETS.TICKET)
                    .from(TICKETS)
                    .where(TICKETS.FUID.eq(ULong.valueOf(userId)))
                    .fetchOne();
            return record.get(TICKETS.TICKET);
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
        return -1;
    }

    public long getUserMoney(String userId) {
        try (DSLContext db = DSL.using(getConnection(), SQLDialect.MYSQL)) {
            Record1<Long> record = db.select(OVERALL_SCORES.MONEY)
                    .from(OVERALL_SCORES)
                    .where(OVERALL_SCORES.FUID.eq(ULong.valueOf(userId)))
                    .fetchOne();
            return record.get(OVERALL_SCORES.MONEY);
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
        return -1;
    }

    public int getUserVipStatus(String userId) {
        try (DSLContext db = DSL.using(getConnection(), SQLDialect.MYSQL)) {
            Record1<Integer> record = db.select(OVERALL_SCORES.ISVIP)
                    .from(OVERALL_SCORES)
                    .where(OVERALL_SCORES.FUID.eq(ULong.valueOf(userId)))
                    .fetchOne();
            return record.get(OVERALL_SCORES.ISVIP);
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
        return -1;
    }

    public void resetLobbyCounts(int nodeId) {
        try (DSLContext db = DSL.using(getConnection(), SQLDialect.MYSQL)) {
            db.update(NODES)
                    .set(NODES.GAME_COUNT, 0).set(NODES.USER_COUNT, 0)
                    .where(NODES.ID.eq(UInteger.valueOf(nodeId)))
                    .execute();
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
    }

    public ArrayList<HalfGameModel> getHalfGames(int nodeId) {
        ArrayList<HalfGameModel> games = new ArrayList<>();
        try (DSLContext db = DSL.using(getConnection(), SQLDialect.MYSQL)) {
            Record[] records = db.select()
                    .from(HALF_GAMES)
                    .where(HALF_GAMES.GAME_SERVER_ID.eq(nodeId))
                    .fetchArray();

            for (Record record : records) {
                HalfGameModel model = record.into(HalfGameModel.class);
                games.add(model);
            }

        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }

        return games;
    }

    public boolean clearHalfGames(int serverId) {
        try (DSLContext db = DSL.using(getConnection(), SQLDialect.MYSQL)) {
            int result = db.deleteFrom(HALF_GAMES)
                    .where(HALF_GAMES.GAME_SERVER_ID.eq(serverId))
                    .execute();
            return result == 1;

        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }

        return false;
    }

    public ArrayList<RoomType> getRoomTypes() {
        try (DSLContext db = DSL.using(getConnection(), SQLDialect.MYSQL)) {
            ArrayList<RoomType> list = new ArrayList<>();
            Record[] records = db.select().from(ALLTABLES).orderBy(ALLTABLES.ID).fetchArray();
            for (Record record : records) {
                RoomType roomType = record.into(RoomType.class);
                list.add(roomType);
            }

            return list;

        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }

        return new ArrayList<>();
    }

    public HashMap<QuestType, ArrayList<QuestModel>> getQuests() {

        HashMap<QuestType, ArrayList<QuestModel>> questModels = new HashMap<>();

        HikariDataSource dataSource = DataSource.getDataSource().get();
        try (DSLContext context = DSL.using(dataSource, SQLDialect.MYSQL)) {
            Result<Record> records = context.select().from(QUESTS).fetch();

            for (Record record : records) {
                QuestModel model = record.into(QuestModel.class);

                try {
                    QuestType questType = QuestType.forCode(record.get("type", Integer.class));

                    questModels.computeIfAbsent(questType, k -> new ArrayList<>());

                    questModels.get(questType).add(model);
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                }
            }

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        return questModels;
    }

    public HashMap<Integer, ArrayList<QuestAwardModel>> getLevelAwards() {

        HashMap<Integer, ArrayList<QuestAwardModel>> awards = new HashMap<>();

        HikariDataSource dataSource = DataSource.getDataSource().get();
        try (DSLContext context = DSL.using(dataSource, SQLDialect.MYSQL)) {
            Result<Record> records = context.select().from(QUEST_AWARD_LEVELS).fetch();

            for (Record record : records) {
                QuestAwardModel questAwardModel = record.into(QuestAwardModel.class);

                int level = record.get("level", Integer.class);
                awards.computeIfAbsent(level, k -> new ArrayList<>());
                awards.get(level).add(questAwardModel);
            }

            return awards;
        } catch (
                Exception e) {
            logger.error(e.getMessage(), e);
        }

        return awards;
    }

    public ConcurrentHashMap<Integer, UserQuestModel> getUserQuests(String userId, int season) {
        ConcurrentHashMap<Integer, UserQuestModel> quests = new ConcurrentHashMap<>();

        try (DSLContext context = DSL.using(getConnection(), SQLDialect.MYSQL)) {
            Result<Record> records = context.select().from(USER_QUESTS)
                    .where(USER_QUESTS.FUID.eq(Long.parseLong(userId)))
                    .and(USER_QUESTS.SEASON.eq(UInteger.valueOf(season)))
                    .fetch();

            for (Record record : records) {
                UserQuestModel userQuestModel = record.into(UserQuestModel.class);
                int questId = record.get("questId", Integer.class);
                quests.put(questId, userQuestModel);
            }

        } catch (
                Exception e) {
            logger.error(e.getMessage(), e);
        }

        return quests;
    }

    public long getUserSeasonPass(String userId, int season) {
        long endDate = -1;

        try (DSLContext context = DSL.using(getConnection(), SQLDialect.MYSQL)) {
            Record record = context.select().from(USER_SEASON_PASS)
                    .where(USER_SEASON_PASS.FUID.eq(Long.parseLong(userId)))
                    .and(USER_SEASON_PASS.SEASON.eq(season))
                    .fetchOne();

            if (record != null) endDate = record.getValue("end_date", Timestamp.class).getTime();

        } catch (
                Exception e) {
            logger.error(e.getMessage(), e);
        }

        return endDate;
    }

    public void getSettings() {
        try (DSLContext context = DSL.using(getConnection(), SQLDialect.MYSQL)) {
            Result<Record> records = context.select().from(QUEST_SETTINGS).fetch();

            for (Record record : records) {
                UserQuestModel userQuestModel = record.into(UserQuestModel.class);
                String name = record.get("name", String.class);
                String value = record.get("value", String.class);
                QuestSettings.setValue(name, value);
            }

        } catch (
                Exception e) {
            logger.error(e.getMessage(), e);
        }
    }


    public QuestUserSeasonModel getQuestUserSeason(String fuid, int season) {
        try (DSLContext context = DSL.using(getConnection(), SQLDialect.MYSQL)) {
            Record record = context.select().from(USER_QUEST_SEASONS)
                    .where(USER_QUEST_SEASONS.FUID.eq(Long.parseLong(fuid)))
                    .and(USER_QUEST_SEASONS.SEASON.eq(UInteger.valueOf(season)))
                    .fetchOne();

            if (record != null) return record.into(QuestUserSeasonModel.class);

        } catch (
                Exception e) {
            logger.error(e.getMessage(), e);
        }

        return null;
    }

    public void addGift(String senderFuid, String receiverFuid, String giftId) {
        try (DSLContext context = DSL.using(getConnection(), SQLDialect.MYSQL)) {

            context.insertInto(GIFTS).
                    columns(GIFTS.SENDERFUID, GIFTS.RECEIVERFUID, GIFTS.GIFTID)
                    .values(ULong.valueOf(senderFuid), ULong.valueOf(receiverFuid), giftId).executeAsync();

            int giftCount = context.select().from(GIFTS).where(GIFTS.RECEIVERFUID.eq(ULong.valueOf(receiverFuid))).fetchCount();

            if (giftCount >= 10) {
                Record oldestGift = context.select().from(GIFTS).where(GIFTS.RECEIVERFUID.eq(ULong.valueOf(receiverFuid))).orderBy(GIFTS.ID.asc()).limit(1).fetchOne();
                if (oldestGift != null) {
                    context.deleteFrom(GIFTS).where(GIFTS.ID.eq(oldestGift.get(GIFTS.ID))).executeAsync();
                }
            }

        } catch (
                Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    public void updateProfileGift(String receiverFuid, String giftId) {
        try (DSLContext context = DSL.using(getConnection(), SQLDialect.MYSQL)) {

            context.update(OVERALL_SCORES).set(OVERALL_SCORES.CURRENT_GIFT, giftId).where(OVERALL_SCORES.FUID.eq(ULong.valueOf(receiverFuid))).executeAsync();
        } catch (
                Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    public void updateGiftCounter(String giftId, String platform) {
        try (DSLContext context = DSL.using(getConnection(), SQLDialect.MYSQL)) {

            Date currentDate = new Date(System.currentTimeMillis());

            Record r = context.select().from(GIFT_COUNT).where(GIFT_COUNT.GIFT_ID.eq(giftId)).and(GIFT_COUNT.PLATFORM.eq(platform)).and(GIFT_COUNT.CREATE_DATE.eq(currentDate)).fetchOne();

            boolean exists = r != null;

            if (exists) {
                context.update(GIFT_COUNT).set(GIFT_COUNT.COUNTER, GIFT_COUNT.COUNTER.add(1)).where(GIFT_COUNT.GIFT_ID.eq(giftId)).and(GIFT_COUNT.PLATFORM.eq(platform)).and(GIFT_COUNT.CREATE_DATE.eq(currentDate)).executeAsync();
            } else {
                context.insertInto(GIFT_COUNT).columns(GIFT_COUNT.GIFT_ID, GIFT_COUNT.CREATE_DATE, GIFT_COUNT.COUNTER, GIFT_COUNT.PLATFORM).values(giftId, currentDate, 1, platform).executeAsync();
            }

        } catch (
                Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    public Hashtable<String, GiftModel> getGifts() {
        Hashtable<String, GiftModel> gifts = new Hashtable<>();

        try (DSLContext context = DSL.using(getConnection(), SQLDialect.MYSQL)) {
            Result<Record> records = context.select().from(GIFT_DATA).fetch();

            for (Record record : records) {
                GiftModel gift = record.into(GiftModel.class);
                gifts.put(gift.getGiftId(), gift);
            }
        } catch (
                Exception e) {
            logger.error(e.getMessage(), e);
        }

        return gifts;
    }

    public HashMap<String, FriendRequestModel> getFriendRequests(String fuid) {
        HashMap<String, FriendRequestModel> friendRequests = new HashMap<>();

        try (DSLContext context = DSL.using(getConnection(), SQLDialect.MYSQL)) {
            Result<Record> records = context.select().from(FRIEND_REQUESTS).where(FRIEND_REQUESTS.INVITED_FUID.eq(Long.parseLong(fuid))).fetch();

            for (Record record : records) {
                FriendRequestModel friendRequest = record.into(FriendRequestModel.class);
                friendRequests.put(friendRequest.getInvitedFuid(), friendRequest);
            }
        } catch (
                Exception e) {
            logger.error(e.getMessage(), e);
        }

        return friendRequests;
    }

    public void addFriendRequest(FriendRequestModel friendRequest) {
        try (DSLContext context = DSL.using(getConnection(), SQLDialect.MYSQL)) {
            context.insertInto(FRIEND_REQUESTS)
                    .columns(FRIEND_REQUESTS.INVITING_FUID, FRIEND_REQUESTS.INVITED_FUID, FRIEND_REQUESTS.STATUS)
                    .values(Long.parseLong(friendRequest.getInvitingFuid()), Long.parseLong(friendRequest.getInvitedFuid()), friendRequest.getStatus())
                    .execute();
        } catch (
                Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    public void removeFriendRequest(String invitingFuid, String invitedFuid) {
        try (DSLContext context = DSL.using(getConnection(), SQLDialect.MYSQL)) {
            context.deleteFrom(FRIEND_REQUESTS)
                    .where(FRIEND_REQUESTS.INVITING_FUID.eq(Long.parseLong(invitingFuid)))
                    .and(FRIEND_REQUESTS.INVITED_FUID.eq(Long.parseLong(invitedFuid)))
                    .execute();
        } catch (
                Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    public void addFriend(String fuid, String friendFuid) {
        try (DSLContext context = DSL.using(getFriendsConnection(), SQLDialect.MYSQL)) {

            Result<Record> r = context.select().from(FRIENDS).where(FRIENDS.FUID.eq(ULong.valueOf(fuid)).and(FRIENDS.FRIENDFUID.eq(ULong.valueOf(friendFuid)))).or(FRIENDS.FUID.eq(ULong.valueOf(friendFuid)).and(FRIENDS.FRIENDFUID.eq(ULong.valueOf(fuid)))).fetch();

            boolean notExists = r.isEmpty();

            if (notExists) {
                context.insertInto(FRIENDS).columns(FRIENDS.FUID, FRIENDS.FRIENDFUID, FRIENDS.ISFBFRIEND).values(ULong.valueOf(fuid), ULong.valueOf(friendFuid), UByte.valueOf(0)).execute();
            }

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    public boolean isFriend(String fuid, String friendFuid) {
        try (DSLContext context = DSL.using(getFriendsConnection(), SQLDialect.MYSQL)) {

            Result<Record> r = context.select().from(FRIENDS).where(FRIENDS.FUID.eq(ULong.valueOf(fuid)).and(FRIENDS.FRIENDFUID.eq(ULong.valueOf(friendFuid)))).or(FRIENDS.FUID.eq(ULong.valueOf(friendFuid)).and(FRIENDS.FRIENDFUID.eq(ULong.valueOf(fuid)))).fetch();

            boolean exist = !r.isEmpty();

            return exist;

        } catch (
                Exception e) {
            logger.error(e.getMessage(), e);
        }

        return false;
    }

    public void removeAllFriendRequests(String fuid) {
        try (DSLContext context = DSL.using(getConnection(), SQLDialect.MYSQL)) {
            context.deleteFrom(FRIEND_REQUESTS)
                    .where(FRIEND_REQUESTS.INVITING_FUID.eq(Long.parseLong(fuid)))
                    .or(FRIEND_REQUESTS.INVITED_FUID.eq(Long.parseLong(fuid)))
                    .execute();
        } catch (
                Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    public void removeFriend(String fuid, String friendFuid) {
        try (DSLContext context = DSL.using(getFriendsConnection(), SQLDialect.MYSQL)) {
            context.deleteFrom(FRIENDS).where(FRIENDS.FUID.eq(ULong.valueOf(fuid)).and(FRIENDS.FRIENDFUID.eq(ULong.valueOf(friendFuid)))).execute();
            context.deleteFrom(FRIENDS).where(FRIENDS.FUID.eq(ULong.valueOf(friendFuid)).and(FRIENDS.FRIENDFUID.eq(ULong.valueOf(fuid)))).execute();
        } catch (
                Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    public NodeData getServerNode(NodesType type, String groupId, ServerType serverType) throws Exception {
        String networkAddressPrefix = ServerConfiguration.get("networkAddressPrefix");

        try {
            String host = Utils.getLocalHostLANAddress(networkAddressPrefix).getHostAddress();
            int port = ServerConfiguration.getInt("TCP.port");
            NodeData nodeData;

            nodeData = getMyNodeInfo(host, port);

            if (nodeData != null) {
                nodeData.setGroupId(groupId);
                nodeData.setServerType(serverType);
                return nodeData;
            } else {
                nodeData = createNodeInfo(host, port, type, groupId);
                nodeData.setGroupId(groupId);
                nodeData.setServerType(serverType);
                return nodeData;
            }

        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }

        throw new Exception("Server Node CAN NOT initialized!");
    }

    public void addZombieUser(String userId, String platform, int nodeId, int exNodeId) {
        try (DSLContext context = DSL.using(getFriendsConnection(), SQLDialect.MYSQL)) {
            context
                    .insertInto(ZOMBI_USER)
                    .columns(ZOMBI_USER.FUID, ZOMBI_USER.PLATFORM, ZOMBI_USER.LOBBY_ID, ZOMBI_USER.EX_ID)
                    .values(Long.valueOf(userId), platform, nodeId, exNodeId)
                    .executeAsync();
        } catch (
                Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    public Long getUserMuted(String userId) {
        try (DSLContext db = DSL.using(getConnection(), SQLDialect.MYSQL)) {
            Record record = db.select(OVERALL_SCORES.MUTED)
                    .from(OVERALL_SCORES)
                    .where(OVERALL_SCORES.FUID.eq(ULong.valueOf(userId)))
                    .fetchOne();
            return record.into(Long.class);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        return null;
    }

    public UserTournamentModel getUserTournamentModel(String userId, int tId) {
        try (DSLContext db = DSL.using(getConnection(), SQLDialect.MYSQL)) {
            Record record = db.select()
                    .from(TOURNAMENT_USER_LEVELS)
                    .where(TOURNAMENT_USER_LEVELS.FUID.eq(Long.valueOf(userId)))
                    .and(TOURNAMENT_USER_LEVELS.TOURID.eq(tId))
                    .fetchOne();
            if (record != null) {
                return record.into(UserTournamentModel.class);
            }

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        return null;
    }

    public UserTournamentModel removeUserTournamentModel(String userId, int tId) {
        try (DSLContext db = DSL.using(getConnection(), SQLDialect.MYSQL)) {
            db.deleteFrom(TOURNAMENT_USER_LEVELS)
                    .where(TOURNAMENT_USER_LEVELS.FUID.eq(Long.valueOf(userId)))
                    .and(TOURNAMENT_USER_LEVELS.TOURID.eq(tId))
                    .execute();

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        return null;
    }

    public void updateUserTournament(String userId, int tournamentId, UserTournamentModel model) {
        try (DSLContext db = DSL.using(getConnection(), SQLDialect.MYSQL)) {

            Record1<Integer> fetchOne = db.selectCount().from(TOURNAMENT_USER_LEVELS)
                    .where(TOURNAMENT_USER_LEVELS.FUID.eq(Long.valueOf(userId)))
                    .and(TOURNAMENT_USER_LEVELS.TOURID.eq(tournamentId))
                    .fetchOne();

            byte lastLevel = model.isLastLevel ? (byte) 1 : (byte) 0;
            byte claimed = model.claimed ? (byte) 1 : (byte) 0;
            if (fetchOne != null && fetchOne.value1() > 0) {
                db.update(TOURNAMENT_USER_LEVELS)
                        .set(TOURNAMENT_USER_LEVELS.AWARD, (long) model.award)
                        .set(TOURNAMENT_USER_LEVELS.IS_LAST_LEVEL, lastLevel)
                        .set(TOURNAMENT_USER_LEVELS.LEVEL, (byte) model.level)
                        .set(TOURNAMENT_USER_LEVELS.CLAIMED, claimed)
                        .set(TOURNAMENT_USER_LEVELS.FAIL, (byte) model.failPoint)
                        .set(TOURNAMENT_USER_LEVELS.LOST, (byte) model.lostGames)
                        .set(TOURNAMENT_USER_LEVELS.REM_TRY_COUNT, (byte) model.remainingTryCount)
                        .set(TOURNAMENT_USER_LEVELS.STATE, (byte) model.state.getValue())
                        .where(TOURNAMENT_USER_LEVELS.FUID.eq(Long.valueOf(userId)))
                        .and(TOURNAMENT_USER_LEVELS.TOURID.eq(tournamentId))
                        .executeAsync();
            } else {
                db.insertInto(TOURNAMENT_USER_LEVELS)
                        .columns(
                                TOURNAMENT_USER_LEVELS.FUID,
                                TOURNAMENT_USER_LEVELS.TOURID,
                                TOURNAMENT_USER_LEVELS.AWARD,
                                TOURNAMENT_USER_LEVELS.IS_LAST_LEVEL,
                                TOURNAMENT_USER_LEVELS.LEVEL,
                                TOURNAMENT_USER_LEVELS.CLAIMED,
                                TOURNAMENT_USER_LEVELS.ENDDATE,
                                TOURNAMENT_USER_LEVELS.FAIL,
                                TOURNAMENT_USER_LEVELS.LOST,
                                TOURNAMENT_USER_LEVELS.REM_TRY_COUNT,
                                TOURNAMENT_USER_LEVELS.STATE,
                                TOURNAMENT_USER_LEVELS.TID
                        )
                        .values(Long.valueOf(userId), tournamentId, (long) model.award, lastLevel, (byte) model.level, claimed, model.endDate, (byte) model.failPoint, (byte) model.lostGames, (byte) model.remainingTryCount, (byte) model.state.getValue(), model.tid)
                        .executeAsync();
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    public Integer getCCU() {
        try (DSLContext db = DSL.using(getConnection(), SQLDialect.MYSQL)) {
            Record record = db.select(ROOMS.USERCOUNTER)
                    .from(ROOMS)
                    .where(ROOMS.ROOMID.eq(1))
                    .fetchOne();
            if (record != null) {
                return record.get(ROOMS.USERCOUNTER);
            }

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        return null;
    }

    public HashSet<String> getUserGifts(String userId) {
        HashSet<String> gifts = new HashSet<>();
        try (DSLContext db = DSL.using(getConnection(), SQLDialect.MYSQL)) {
            Record[] records = db.select(GIFTS.GIFTID)
                    .from(GIFTS)
                    .where(GIFTS.RECEIVERFUID.eq(ULong.valueOf(userId)))
                    .and(GIFTS.STATUS.eq(UInteger.valueOf(0)))
                    .orderBy(GIFTS.CREATEDATE)
                    .fetchArray();

            for (Record record : records) {
                String giftId = record.into(String.class);
                gifts.add(giftId);
            }

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return gifts;
    }

    public void updateUserSettings(String id,SettingsTypes type, int status) {
        try (DSLContext db = DSL.using(getConnection(), SQLDialect.MYSQL)) {

            if (type.equals(SettingsTypes.PRIVATE_CHAT)) {
                db.update(MEMBERS)
                        .set(MEMBERS.PRIVATE_CHAT, UByte.valueOf(status))
                        .where(MEMBERS.FUID.eq(ULong.valueOf(id)))
                        .execute();
            }else if(type.equals(SettingsTypes.GO_PROFILE)) {
                db.update(MEMBERS)
                        .set(MEMBERS.GO_PROFILE, UByte.valueOf(status))
                        .where(MEMBERS.FUID.eq(ULong.valueOf(id)))
                        .execute();
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

    }

    public UserTournamentStats getTournamentProfile(String id, int tournamentId) {
        try (DSLContext db = DSL.using(getConnection(), SQLDialect.MYSQL)) {
            Record record = db.select()
                    .from(TOURNAMENT_HISTORY)
                    .where(TOURNAMENT_HISTORY.FUID.eq(Long.valueOf(id)))
                    .and(TOURNAMENT_HISTORY.TOURNAMENT_ID.eq(tournamentId))
                    .fetchOne();
            if (record != null) {
                return record.into(UserTournamentStats.class);
            }

        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }

        return null;
    }

    public ArrayList<UserTournamentStats> setTournamentProfile(String id, int tournamentId) {
        try (DSLContext db = DSL.using(getConnection(), SQLDialect.MYSQL)) {
            Timestamp now = new Timestamp(System.currentTimeMillis());

            db.insertInto(TOURNAMENT_HISTORY).
                    columns(TOURNAMENT_HISTORY.FUID, TOURNAMENT_HISTORY.TOURNAMENT_ID, TOURNAMENT_HISTORY.MAX_LEVEL, TOURNAMENT_HISTORY.TOTAL_TOURNAMENT, TOURNAMENT_HISTORY.WON_TOURNAMENT, TOURNAMENT_HISTORY.TOTAL_GAME, TOURNAMENT_HISTORY.WON_GAME, TOURNAMENT_HISTORY.CREATE_DATE, TOURNAMENT_HISTORY.UPDATE_DATE)
                    .values(Long.parseLong(id), tournamentId, 1, 0, 0, 0, 0, now, now).executeAsync();

        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }

        return null;
    }

    public ArrayList<UserTournamentStats> getTournamentProfile(String id) {
        try (DSLContext db = DSL.using(getConnection(), SQLDialect.MYSQL)) {
            Result<Record> records = db.select()
                    .from(TOURNAMENT_HISTORY)
                    .where(TOURNAMENT_HISTORY.FUID.eq(Long.valueOf(id)))

                    .fetch();
            if (records != null) {
                return new ArrayList<UserTournamentStats>(records.into(UserTournamentStats.class));
            }

        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }

        return null;
    }

    public List<BonusModel> getBonuses() {
        try (DSLContext db = DSL.using(getConnection(), SQLDialect.MYSQL)) {
            Result<Record> records = db.select()
                    .from(BONUSES_TEST)
                    .orderBy(BONUSES_TEST.ID)
                    .fetch();
            if (records != null) {
                return records.into(BonusModel.class);
            }

        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }

        return new ArrayList<>();
    }

    public HashMap<Integer, UserBonusInfo> getUserBonuses(String fuid) {
        HashMap<Integer, UserBonusInfo> map = new HashMap();

        try (DSLContext db = DSL.using(getConnection(), SQLDialect.MYSQL)) {
            Result<Record> records = db.select()
                    .from(USER_BONUSES)
                    .where(USER_BONUSES.FUID.eq(Long.valueOf(fuid)))
                    .fetch();

            for (Record record : records) {
                UserBonusInfo model = record.into(UserBonusInfo.class);
                map.put(model.getBonusId(), model);
            }

        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }

        return map;
    }

    public boolean checkUserBonus(String fuid, UserBonusInfo info){
        try (DSLContext db = DSL.using(getConnection(), SQLDialect.MYSQL)) {
            Result<Record> records = db.select()
                    .from(USER_BONUSES)
                    .where(USER_BONUSES.FUID.eq(Long.valueOf(fuid)))
                    .and(USER_BONUSES.BONUS_ID.eq(info.getBonusId()))
                    .fetch();

            return !records.isEmpty();

        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }

        return false;
    }

    public void addUserBonus(String fuid, UserBonusInfo info) {
        try (DSLContext db = DSL.using(getConnection(), SQLDialect.MYSQL)) {

            int result = db.insertInto(USER_BONUSES).
                    columns(USER_BONUSES.FUID, USER_BONUSES.COUNT, USER_BONUSES.BONUS_ID)
                    .values(Long.parseLong(fuid), info.getCount(), info.getBonusId()).execute();

        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
    }

    public void updateUserBonus(String fuid, UserBonusInfo info) {
        try (DSLContext db = DSL.using(getConnection(), SQLDialect.MYSQL)) {
            int result = db.update(USER_BONUSES)
                    .set(USER_BONUSES.COUNT, info.getCount())
                    .where(USER_BONUSES.FUID.eq(Long.valueOf(fuid)))
                    .and(USER_BONUSES.BONUS_ID.eq(info.getBonusId()))
                    .execute();
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }
    }

    public HashMap<String, String> getNotificationUrls(){
        HashMap<String, String> notificationUrls = new HashMap<>();

        try (DSLContext context = DSL.using(getConnection(), SQLDialect.MYSQL)) {
            Result<Record> records = context.select().from(NOTIFICATIONS).fetch();

            for (Record record : records) {
                String title = record.get("title", String.class);
                String url = record.get("url", String.class);
                notificationUrls.put(title, url);
            }
        } catch (
                Exception e) {
            logger.error(e.getMessage(), e);
        }

        return notificationUrls;
    }
}
