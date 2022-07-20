package com.mynet.questservice;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mynet.questservice.consumer.QuestEventQueue;
import com.mynet.questservice.db.queue.QuestDbQueue;
import com.mynet.questservice.db.queue.UpdateUserQuestQuery;
import com.mynet.questservice.db.queue.UpdateUserSeasonQuery;
import com.mynet.questservice.quests.category.DifferentDaysCategoryInfo;
import com.mynet.questservice.quests.category.QuestCategory;
import com.mynet.questservice.quests.category.QuestCategoryInfo;
import com.mynet.questservice.quests.messages.*;
import com.mynet.questservice.quests.models.*;
import com.mynet.questservice.quests.ruleSets.*;
import com.mynet.questservice.quests.types.*;
import com.mynet.shared.config.ServerConfiguration;
import com.mynet.shared.connection.NodeToProxy;
import com.mynet.shared.logs.ExperienceLog;
import com.mynet.shared.logs.RabbitMQThread;
import com.mynet.shared.network.GameCommands;
import com.mynet.shared.network.NetworkMessage;
import com.mynet.shared.resource.CacheController;
import com.mynet.shared.resource.db.DBController;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class QuestController {
    private static Logger logger = LoggerFactory.getLogger(QuestController.class);
    private DBController db;
    private HashMap<QuestCategory, RuleSet> questRulesMap;
    private HashMap<QuestType, ArrayList<QuestModel>> questModels;
    private HashMap<Integer, QuestModel> quests;
    private HashMap<Integer, ArrayList<QuestAwardModel>> levelAwards; //Key is level id
    private HashMap<Integer, Integer> levelXps; // Key is level id
    private HashMap<Integer, QuestAwardModel> awards; //Key is award id
    private ArrayList<Integer> basicLevelIds;
    private ConcurrentHashMap<String, QuestUser> users;
    private QuestEventQueue eventQueue;
    private RabbitConsumerThread rabbitConsumerThread;
    private static Gson gson;
    private boolean readyForConsume;
    private QuestSeasonModel season;
    private QuestDbQueue dbQueue;
    private CacheController cacheController;
    private RabbitMQThread experienceLogDbThread;
    private NodeToProxy nodeToProxy;
    private int connectedProxyCount;
    private HealthCheckThread healthCheckThread;

    private static QuestController instance;

    public static void init(){
        if(instance == null){
            instance = new QuestController();
        }
    }

    public static QuestController getInstance() {
        return instance;
    }

    private QuestController() {
        users = new ConcurrentHashMap<>();
        db = DBController.getInstance();
        dbQueue = new QuestDbQueue();
        eventQueue = new QuestEventQueue(this);
        cacheController = CacheController.getInstance();
        season = cacheController.getSeason();
        this.nodeToProxy = new NodeToProxy();
        initExpLogThread();
        getDbModels();
        registerRuleSets();
        setConsumer();
        addRedisEventListeners();
    }

    public void sendMessage(NetworkMessage message, QuestUser user) {
        message.setId(user.getId());
        nodeToProxy.addServerMessage(message, user);
    }

    private void setConsumer() {
        String host = ServerConfiguration.get("RabbitMqHost");
        String username = ServerConfiguration.get("RabbitMqUsername");
        String pass = ServerConfiguration.get("RabbitMqPass");
        String queue = ServerConfiguration.get("RabbitMqQuestLog");
        rabbitConsumerThread = new RabbitConsumerThread(queue, host, username, pass);
    }

    public void controlConsumer(boolean ready) {
        if (ready) {
            readyForConsume = true;
            rabbitConsumerThread.start();
        } else {
            readyForConsume = false;
            rabbitConsumerThread.stop();
        }
    }

    public void initHealthCheck(){
        healthCheckThread = new HealthCheckThread();
        healthCheckThread.start();
    }

    public boolean isReady() {
        return readyForConsume;
    }

    private void getDbModels() {
        questModels = db.getQuests();
        levelAwards = db.getLevelAwards();
        db.getSettings();

        awards = new HashMap<>();
        levelXps = new HashMap<>();

        for (Map.Entry<Integer, ArrayList<QuestAwardModel>> level : levelAwards.entrySet()) {
            ArrayList<QuestAwardModel> awardsOfLevel = level.getValue();

            for (int i = 0; i < awardsOfLevel.size(); i++) {
                QuestAwardModel award = awardsOfLevel.get(i);
                awards.put(award.getId(), award);
            }

            levelXps.put(level.getKey(), getLevelGoal(level.getKey()));

        }

        quests = new HashMap<>();
        for (ArrayList<QuestModel> list : questModels.values()) {
            for (QuestModel quest : list) {
                quests.put(quest.getId(), quest);
            }
        }

        basicLevelIds = new ArrayList<>();
        for (ArrayList<QuestAwardModel> list : levelAwards.values()) {
            for (QuestAwardModel model : list) {
                if (!model.isPass()) {
                    basicLevelIds.add(model.getId());
                }
            }
        }
    }

    public boolean isBasicAward(int id) {
        return basicLevelIds.contains(id);
    }

    public QuestModel getQuest(int id) {
        return quests.get(id);
    }

    public int getLevelGoal(int level) {
        return levelAwards.get(level).get(0).getXp();
    }

    public ArrayList<QuestAwardModel> getLevelAwards(int level) {
        return levelAwards.get(level);
    }

    public boolean isInitialAward(int id) {
        return levelAwards.get(1).stream().filter(x -> x.getId() == id).count() > 0;
    }

    public boolean addUser(QuestUser user) {
        String userID = user.getId();

        if (users.containsKey(userID)) {
            return false;
        }

        users.put(userID, user);

        return true;
    }

    public QuestUser getUser(String id) {
        return users.get(id);
    }

    public QuestUser createUser(String userId) {
        removeUser(userId);

        QuestUser user = new QuestUser(userId);
        int proxyID = cacheController.getUserProxyNode(userId);
        user.setProxyID(proxyID);
        setUser(user,false);

        addUser(user);

        checkDifferentDaysLogin(user.getId());

        return user;
    }

    public void setUser(QuestUser user, boolean previousSeasonOpen) {
        String userId = user.getId();
        ConcurrentHashMap<Integer, UserQuestModel> userQuests = db.getUserQuests(userId, season.getId());
        for (UserQuestModel model : userQuests.values()) {
            QuestModel quest = getQuest(model.getQuestId());
            if(quest != null) {
                model.setGoal(quest.getGoal());
            }
        }
        user.setQuests(userQuests);

        long seasonPassEndDate = db.getUserSeasonPass(user.getId(), season.getId());
        user.setSeasonPassEndDate(seasonPassEndDate);

        Map<Integer, Integer> userSeason = cacheController.getUserSeason(userId, season.getId());

        int level = 1;
        for (int awardId : userSeason.keySet()) {
            if (isBasicAward(awardId) && !isInitialAward(awardId)) {
                level++;
            }
        }

        level = Math.min(levelXps.size(), level);

        boolean firstTime = false;

        QuestUserSeasonModel questUserSeasonModel = db.getQuestUserSeason(userId, season.getId());
        if (questUserSeasonModel == null) {
            questUserSeasonModel = new QuestUserSeasonModel(userId, 0, level, season.getId(), 0);
            addInsertQuestUserSeason(questUserSeasonModel);
            firstTime = true;
        }

        user.setFirstTime(firstTime);

        int userXP = questUserSeasonModel.getXp();

        UserQuestLevelModel userQuestLevelModel = new UserQuestLevelModel(userXP, new ConcurrentHashMap<>(userSeason), level);

        user.setQuestLevelModel(userQuestLevelModel);

        if (firstTime) {
            ArrayList<QuestAwardModel> initialAwards = levelAwards.get(1);

            for (QuestAwardModel award : initialAwards) {
                if(!award.isPass()){
                    user.getQuestLevelModel().addAward(award.getId());
                    setAward(user, award, season.getId());
                }
            }
        }

        if (System.currentTimeMillis() < user.getSeasonPassEndDate()) {

            for (int i = 1; i <= level; i++) {
                for (QuestAwardModel award : getLevelAwards(i)) {

                    if (award.isPass() && userSeason.get(award.getId()) == null) {
                        user.getQuestLevelModel().addAward(award.getId());
                        setAward(user, award, season.getId());
                    }

                }
            }

        }

        int doubleXp = questUserSeasonModel.getDoubleXp();

        user.setDoubleXp(doubleXp);

        UserPreviousSeasonModel userPreviousSeason = getUserPreviousSeason(user, season, previousSeasonOpen);

        user.setUserPreviousSeason(userPreviousSeason);

        int lastPlayDay = cacheController.getDifferentDaysPlay(user.getId(), season.getId());

        user.setLastPlayDay(lastPlayDay);

        int winningStreak = cacheController.getWinningStreak(user.getId(), season.getId());

        user.setWinningStreak(winningStreak);

        int lastLoginDay = cacheController.getDifferentDaysLogin(user.getId(), season.getId());

        user.setLastLoginDay(lastLoginDay);

        user.setSeasonId(season.getId());

        //MAYBE WE USE IT LATER
        /*int extraXpAmount = cacheController.getExtraXpAmount(userId,season.getId());
        user.setExtraXpAmount(extraXpAmount);

        int extraXpAward = cacheController.getExtraXpAward(userId,season.getId());
        user.setExtraXpAward(extraXpAward);*/
    }

    public void removeUser(String userId) {
        users.remove(userId);
    }

    public void fixUser(String userId) {
        if (!users.containsKey(userId)) {
            createUser(userId);
        }
    }

    public void setAward(QuestUser user, QuestAwardModel award, int id) {
        cacheController.addUserSeasonAwardNotification(user.getId());
        cacheController.setUserSeasonAward(user.getId(), id, award.getId());
    }


    public void registerRuleSets() {
        questRulesMap = new HashMap<>();
        questRulesMap.put(QuestCategory.WON_GAME, new WonGameRuleSet());
        questRulesMap.put(QuestCategory.DIFFERENT_DAYS, new DiffenrentDaysRuleSet());
        questRulesMap.put(QuestCategory.BOT, new BotRuleSet());
        questRulesMap.put(QuestCategory.END_GAME, new EndGameRuleSet());
        questRulesMap.put(QuestCategory.GAME_ACTION, new GameActionRuleSet());
        questRulesMap.put(QuestCategory.MONEY, new MoneyRuleSet());
        questRulesMap.put(QuestCategory.SOCIAL, new SocialRuleSet());
        questRulesMap.put(QuestCategory.STREAK, new StreakRuleSet());
        questRulesMap.put(QuestCategory.TOURNAMENT, new TournamentRuleSet());
        questRulesMap.put(QuestCategory.LOST_GAME, new LostGameRuleSet());
    }

    public ArrayList<QuestModel> getModels(QuestType type) {
        return questModels.get(type);
    }

    public QuestSeasonModel getSeason() {
        return season;
    }

    public void addMessage(String message) {
        eventQueue.add(message);
    }

    public void check(String message) {
        try {
            JSONObject jsonObject = new JSONObject(message);

            QuestCategory category = QuestCategory.valueOf(jsonObject.getString("category"));
            String info = jsonObject.getString("info");
            RuleSet ruleSet = questRulesMap.get(category);
            ruleSet.checkRules(info);
        } catch (Exception ex) {

        }
    }

    public static Gson getGson() {
        if (gson == null) {
            GsonBuilder gsonBuilder = new GsonBuilder();
            gson = gsonBuilder.create();
        }
        return gson;
    }

    public QuestDbQueue getDbQueue() {
        return dbQueue;
    }

    public void addUpdateUserQuestQuery(String fuid, int questId, int point) {
        UpdateUserQuestQuery query = new UpdateUserQuestQuery(fuid, questId, season.getId(), point);
        dbQueue.addStatement(query);
    }

    public void addInsertUserQuestQuery(String fuid, int questId, int point) {
        UpdateUserQuestQuery query = new UpdateUserQuestQuery(fuid, questId, season.getId(), point, true);
        dbQueue.addStatement(query);
    }

    public void gainXp(QuestUser user, int gainedXp, ExpSourceType source, int questId) {

        int initialGainedXp = gainedXp;
        int startXp = user.getQuestLevelModel().getXp();
        int endXp = startXp + gainedXp;
        int currentLevel = user.getQuestLevelModel().getLevel();
        int currentGoalXp = getLevelGoal(currentLevel);
        boolean isLevelUp = false;
        ArrayList<QuestInfoAwardModel> availableAwards = new ArrayList();

        int lastLevel = levelXps.size();
        int lastLevelGoal = getLevelGoal(lastLevel);
        int difference = 0;

        do {
            if ((user.getQuestLevelModel().getLevel() == lastLevel) && (user.getQuestLevelModel().getXp() == lastLevelGoal)) {

                //MAYBE WE USE IT LATER
               /* int totalExtraXp = user.getExtraXpAmount() + gainedXp;

                int requiredExtraXp = Prop.getInt("requiredExtraXp", 500);
                int extraXpAward = Prop.getInt("extraXpAward", 2500);
                int extraXpAwardLimit = Prop.getInt("extraXpAwardLimit", 50000);

                int amount = 0;

                while (totalExtraXp >= requiredExtraXp){

                    if (user.getExtraXpAward() < extraXpAwardLimit) {
                        amount += extraXpAward;

                        int award = (user.getExtraXpAmount() + extraXpAward) > extraXpAwardLimit ? extraXpAwardLimit : user.getExtraXpAmount() + extraXpAward;
                        user.setExtraXpAward(award);
                        cacheController.setExtraXpAward(user.getFuid(), season.getId(), award);
                    }
                    totalExtraXp -= requiredExtraXp;

                }

                user.setExtraXpAmount(totalExtraXp);
                cacheController.setExtraXpAmount(user.getFuid(), season.getId(), totalExtraXp);

                if(amount > 0) {
                    ExtraXpAwardMessage extraXpAwardMessage = new ExtraXpAwardMessage(amount, user.getExtraXpAward());
                    NetworkMessage response = new NetworkMessage();
                    response.setDataAsJSON(extraXpAwardMessage);
                    response.setCmd(NetworkCommands.EXTRA_XP_AWARD);
                    sendMessage(response, user);
                }*/

                gainedXp = 0;
            } else {
                if (endXp >= currentGoalXp) {
                    isLevelUp = true;
                    gainedXp = endXp - currentGoalXp;
                    endXp = user.getQuestLevelModel().getLevel() == lastLevel ? lastLevelGoal : 0;
                    difference = currentGoalXp - startXp;

                    if (getLevelAwards(currentLevel + 1) != null) {
                        for (QuestAwardModel award : getLevelAwards(currentLevel + 1)) {
                            boolean isPass = System.currentTimeMillis() < user.getSeasonPassEndDate();

                            if (!award.isPass() || isPass) {
                                user.getQuestLevelModel().addAward(award.getId());
                                cacheController.setUserSeasonAward(user.getId(), season.getId(), award.getId());
                                QuestInfoAwardModel availableAward = new QuestInfoAwardModel(award, AwardStatusType.AVAILABLE.getValue(), season.getId());
                                availableAwards.add(availableAward);
                            }
                        }
                    }

                } else {
                    difference = gainedXp;
                    gainedXp = 0;
                }

                int nextLevel = user.getQuestLevelModel().getLevel() == lastLevel ? currentLevel : currentLevel + 1;
                int nextGoalXp = getLevelGoal(nextLevel);

                //UserQuestLevelInfoModel oldLevelModel = new UserQuestLevelInfoModel(startXp, currentLevel, currentGoalXp, user.getQuestLevelModel().getTotalXp());

                int newGoalXp = isLevelUp ? nextGoalXp : currentGoalXp;
                int newLevel = isLevelUp ? nextLevel : currentLevel;
                //UserQuestLevelInfoModel newLevelModel = new UserQuestLevelInfoModel(endXp, newLevel, newGoalXp, user.getQuestLevelModel().getTotalXp());

                user.getQuestLevelModel().setXp(endXp);
                user.getQuestLevelModel().setLevel(newLevel);
                user.getQuestLevelModel().setTotalXp(user.getQuestLevelModel().getTotalXp() + difference);

                /*UpdateXpMessage updateXpMessage = new UpdateXpMessage(oldLevelModel, newLevelModel, isLevelUp, getSeasonExpire());

                NetworkMessage levelResponse = new NetworkMessage();
                levelResponse.setDataAsJSON(updateXpMessage);
                levelResponse.setCmd(NetworkCommands.QUEST_XP_UPDATED);
                sendMessage(levelResponse, user);*/

                if (isLevelUp) {
                    currentLevel = nextLevel;
                    currentGoalXp = getLevelGoal(currentLevel);
                    startXp = endXp;
                    endXp = startXp + gainedXp;
                    isLevelUp = false;
                }
            }
        } while (gainedXp > 0);

        addUpdateQuestUserSeason(user);

        if (!availableAwards.isEmpty()) {
            UpdateAwardsMessage updateAwardsMessage = new UpdateAwardsMessage(availableAwards);

            NetworkMessage levelResponse = new NetworkMessage(GameCommands.AWARDS_UPDATED);
            levelResponse.setDataAsJSON(updateAwardsMessage);
            sendMessage(levelResponse, user);
        }

        ExperienceLog log = new ExperienceLog(Long.parseLong(user.getId()), initialGainedXp, user.getQuestLevelModel().getTotalXp(), questId, season.getDay(), source.getValue(), season.getId(), user.getPlatform());
        addExpLog(log);

    }

    public void sendQuestCompleteMessage(QuestUser user, QuestModel quest, int usedDoubleXpAmount, int remainingDoubleXp, boolean levelUp, UserQuestLevelInfoModel oldLevel) {
        long ttl = -1;

        if (quest.isDaily()) {
            long endTime = season.getStartTime() + TimeUnit.DAYS.toMillis(quest.getDay());
            ttl = endTime - System.currentTimeMillis();
        }

        UserQuestModel userQuest = user.getQuest(quest.getId());

        if (userQuest != null) {
            int point = userQuest.getPoint();
            int goal = quest.getGoal();

            QuestInfoModel questInfoModel = new QuestInfoModel(quest.getId(), quest.getDescription(), QuestStatusType.COMPLETED.getValue(), quest.getType().getValue(), quest.getXp(), ttl, point, goal, quest.isDaily());
            UserQuestLevelInfoModel newLevel = new UserQuestLevelInfoModel(user.getQuestLevelModel(), getLevelGoal(user.getQuestLevelModel().getLevel()));
            UpdateXpMessage userLevel = new UpdateXpMessage(oldLevel, newLevel, levelUp, getSeasonExpire());

            boolean awardAvailable = user.getQuestLevelModel().awardAvailable();

            UpdateQuestMessage.Builder builder = new UpdateQuestMessage.Builder();
            UpdateQuestMessage updateQuestMessage = builder.setQuest(questInfoModel)
                    .setUsedDoubleXpAmount(usedDoubleXpAmount)
                    .setRemainingDoubleXpAmount(remainingDoubleXp)
                    .setUserLevel(userLevel)
                    .setAwardAvailable(awardAvailable)
                    .build();

            NetworkMessage questResponse = new NetworkMessage(GameCommands.QUEST_COMPLETED);
            questResponse.setDataAsJSON(updateQuestMessage);
            sendMessage(questResponse, user);
        }
    }

    public ArrayList<QuestInfoAwardModel> getCurrentSeasonAwards(QuestUser user) {
        ArrayList<QuestInfoAwardModel> currentSeasonAwards = new ArrayList<>();

        for (QuestAwardModel award : awards.values()) {
            int status = AwardStatusType.NOT_AVAILABLE.getValue();
            if (user.getQuestLevelModel().getAwards().get(award.getId()) != null) {
                int awardValue = user.getQuestLevelModel().getAwards().get(award.getId());
                status = awardValue == 0 ? AwardStatusType.AVAILABLE.getValue() : AwardStatusType.CLAIMED.getValue();
            }

            QuestInfoAwardModel questInfoAwardModel = new QuestInfoAwardModel(award.getId(), award.getTitle(), award.isPass(), award.getType().getValue(), award.getXp(), award.getLevel(), award.getAward(), status, season.getId(), award.getImageName());
            currentSeasonAwards.add(questInfoAwardModel);
        }

        sortAwards(currentSeasonAwards);
        return currentSeasonAwards;
    }

    public ArrayList<QuestInfoAwardModel> getPreviousSeasonAwards(QuestUser user, boolean hasPass, int level, boolean previousSeasonOpen) {
        ArrayList<QuestInfoAwardModel> previousSeasonAwards = new ArrayList<>();

        if (season.getId() > 1) {
            int previousSeason = season.getId() - 1;
            Map<Integer, Integer> previousSeasonAchievedAwards = CacheController.getInstance().getUserSeason(user.getId(), previousSeason);

            if (hasPass) {

                for (int i = 1; i <= level; i++) {
                    for (QuestAwardModel award : getLevelAwards(i)) {

                        if (award.isPass() && previousSeasonAchievedAwards.get(award.getId()) == null) {
                            previousSeasonAchievedAwards.put(award.getId(), 0);
                            setAward(user, award, previousSeason);
                        }

                    }
                }

            }

            if (!previousSeasonAchievedAwards.isEmpty()) {
                boolean available = false;

                for (QuestAwardModel award : awards.values()) {
                    int status = AwardStatusType.NOT_AVAILABLE.getValue();

                    if (previousSeasonAchievedAwards.get(award.getId()) != null) {
                        int awardValue = previousSeasonAchievedAwards.get(award.getId());
                        status = awardValue == 0 ? AwardStatusType.AVAILABLE.getValue() : AwardStatusType.CLAIMED.getValue();

                        if (status == AwardStatusType.AVAILABLE.getValue()) available = true;
                    }

                    QuestInfoAwardModel questInfoAwardModel = new QuestInfoAwardModel(award.getId(), award.getTitle(), award.isPass(), award.getType().getValue(), award.getXp(), award.getLevel(), award.getAward(), status, previousSeason, award.getImageName());
                    previousSeasonAwards.add(questInfoAwardModel);
                }

                if (!available && !user.isFirstTime() && !previousSeasonOpen) {
                    previousSeasonAwards.clear();
                }
            }

            sortAwards(previousSeasonAwards);
        }

        return previousSeasonAwards;
    }

    public HashMap<QuestCategory, RuleSet> getQuestRulesMap() {
        return questRulesMap;
    }

    public HashMap<Integer, Integer> getLevelXps() {
        return levelXps;
    }

    public long getSeasonExpire() {
        return season.getEndTime() - System.currentTimeMillis();
    }

    public ArrayList<QuestInfoModel> getUserQuests(QuestUser user) {
        ArrayList<QuestInfoModel> userQuests = new ArrayList<>();

        for (QuestModel quest : quests.values()) {
            UserQuestModel userQuest = user.getQuest(quest.getId());

            if (quest.getGoal() == -1) continue;
            if (quest.isDaily() && quest.getDay() != season.getDay()) continue;

            int status = QuestStatusType.ACTIVE.getValue();
            if (!quest.isDaily() && (season.getDay() < 8 && quest.getDay() >= 8))
                status = QuestStatusType.NOT_ACTIVE.getValue();
            else if (userQuest != null && userQuest.getPoint() == userQuest.getGoal())
                status = QuestStatusType.COMPLETED.getValue();

            long ttl = -1;

            if (quest.isDaily()) {
                long endTime = season.getStartTime() + TimeUnit.DAYS.toMillis(quest.getDay());
                ttl = endTime - System.currentTimeMillis();
            }

            int point = userQuest == null ? 0 : userQuest.getPoint();
            int goal = quest.getGoal();

            QuestInfoModel questInfoModel = new QuestInfoModel(quest.getId(), quest.getDescription(), status, quest.getType().getValue(), quest.getXp(), ttl, point, goal, quest.isDaily());
            userQuests.add(questInfoModel);
        }

        userQuests = sortQuests(userQuests);

        return userQuests;
    }


    public ArrayList<QuestInfoModel> sortQuests(ArrayList<QuestInfoModel> quests) {
        Collections.sort(quests, Comparator.comparing(QuestInfoModel::getStatus)
                .thenComparing(Comparator.comparing(QuestInfoModel::isDaily).reversed())
                .thenComparing(Comparator.comparing(QuestInfoModel::getCompletion).reversed()
                .thenComparing(Comparator.comparing(QuestInfoModel::getXp).reversed())));

        return quests;
    }

    public ArrayList<QuestInfoAwardModel> sortAwards(ArrayList<QuestInfoAwardModel> awards) {
        Collections.sort(awards, Comparator.comparing(QuestInfoAwardModel::getLevel));
        return awards;
    }

    private void initExpLogThread() {
        String experienceLogQueueHost = ServerConfiguration.get("RabbitMqHost");
        String experienceLogQueueUsername = ServerConfiguration.get("RabbitMqUsername");
        String experienceLogQueuePass = ServerConfiguration.get("RabbitMqPass");
        String experienceLogQueueName = ServerConfiguration.get("RabbitMqExpLog");

        this.experienceLogDbThread = new RabbitMQThread(experienceLogQueueName, experienceLogQueueHost, experienceLogQueueUsername, experienceLogQueuePass);
    }

    private void addExpLog(ExperienceLog log) {
        this.experienceLogDbThread.addQuery(log);
    }

    public SeasonInfoResponseMessage getSeasonInfo(QuestUser user, boolean open, boolean previousSeasonOpen) {

        user.setFirstTime(false);

        if (user.getUserPreviousSeason() != null) {
            UserPreviousSeasonModel userPreviousSeason = getUserPreviousSeason(user, season, previousSeasonOpen);
            user.setUserPreviousSeason(userPreviousSeason);
        }

        UserQuestLevelModel questLevelModel = user.getQuestLevelModel();

        ArrayList<QuestInfoAwardModel> currentSeasonAwards = getCurrentSeasonAwards(user);

        ArrayList<QuestInfoModel> quests = getUserQuests(user);

        QuestSeasonModel season = getSeason();

        long nextQuestsTtl = -1;

        //Some seasonal quests will be available at second week of the season
        if (season.getDay() <= 7) {
            nextQuestsTtl = season.getStartTime() + TimeUnit.DAYS.toMillis(7) - System.currentTimeMillis();
        }

        boolean isPass = System.currentTimeMillis() < user.getSeasonPassEndDate();

        boolean availableAwards = questLevelModel.getAwards().values().stream().filter(x -> x == 0).count() > 0;

        SeasonInfoResponseMessage.Builder builder = new SeasonInfoResponseMessage.Builder();
        SeasonInfoResponseMessage seasonInfoResponseMessage = builder.season(season.getId())
                .seasonTtl(getSeasonExpire())
                .userLevel(new UserQuestLevelInfoModel(questLevelModel, getLevelGoal(questLevelModel.getLevel())))
                .nextQuestsTtl(nextQuestsTtl)
                .availableAward(availableAwards)
                .currentSeasonAwards(currentSeasonAwards)
                .hasPass(isPass)
                .levelXps(getLevelXps())
                .userPreviousSeason(user.getUserPreviousSeason())
                .quests(quests)
                .seasonHandShake(user.isFirstTime())
                .open(open)
                .seasonCompleted(questLevelModel.getLevel() == basicLevelIds.size())
                .build();

        return seasonInfoResponseMessage;
    }

    public UserPreviousSeasonModel getUserPreviousSeason(QuestUser user, QuestSeasonModel season, boolean previousSeasonOpen) {
        UserPreviousSeasonModel userPreviousSeason = null;

        if (season.getId() > 1) {
            boolean previousPass = db.getUserSeasonPass(user.getId(),season.getId()-1) != -1;
            UserQuestLevelInfoModel previousUserLevel = getUserPreviousLevel(user.getId());
            ArrayList<QuestInfoAwardModel> previousSeasonAwards = getPreviousSeasonAwards(user, previousPass, previousUserLevel.getLevel(), previousSeasonOpen);

            if (!previousSeasonAwards.isEmpty()) {
                userPreviousSeason = new UserPreviousSeasonModel(previousUserLevel, previousSeasonAwards, previousPass, this.levelXps);
            }

        }

        return userPreviousSeason;
    }

    public QuestUserInfoMessage getQuestUserInfo(QuestUser user, boolean seasonFinished) {
        //Delete unnecessary previous season data from cache
        if (user.isFirstTime() && season.getId() > 2) {
            for (int i = 1; i < season.getId() - 1; i++) {
                cacheController.deleteUserSeason(user.getId(), i);
            }
        }

        UserQuestLevelModel questLevelModel = user.getQuestLevelModel();

        boolean availableAwards = questLevelModel.getAwards().values().stream().filter(x -> x == 0).count() > 0;

        return new QuestUserInfoMessage(new UserQuestLevelInfoModel(questLevelModel, getLevelGoal(questLevelModel.getLevel())), getSeasonExpire(), availableAwards, user.getUserPreviousSeason(), user.isFirstTime(), seasonFinished);
    }

    public void updateUserXp(QuestModel model, QuestUser user, UserQuestModel quest, ExpSourceType source) {
        int gainedXp = model.getXp();
        int remainingDoubleXp = user.getDoubleXp();
        int usedDoubleXp = 0;

        if (remainingDoubleXp > 0) {
            if (remainingDoubleXp >= gainedXp) {
                usedDoubleXp = gainedXp;
                remainingDoubleXp -= usedDoubleXp;
                gainedXp *= 2;
            } else {
                gainedXp += remainingDoubleXp;
                usedDoubleXp = remainingDoubleXp;
                remainingDoubleXp = 0;
            }
            user.setDoubleXp(remainingDoubleXp);
            addUpdateQuestUserSeason(user);
        }

        UserQuestLevelInfoModel oldLevel = new UserQuestLevelInfoModel(user.getQuestLevelModel(), getLevelGoal(user.getQuestLevelModel().getLevel()));

        gainXp(user, gainedXp, source, quest.getQuestId());

        boolean levelUp = oldLevel.getLevel() != user.getQuestLevelModel().getLevel();

        sendQuestCompleteMessage(user, model, usedDoubleXp, remainingDoubleXp, levelUp, oldLevel);
    }

    private void addUpdateQuestUserSeason(QuestUser user) {
        UserQuestLevelModel levelModel = user.getQuestLevelModel();
        UpdateUserSeasonQuery query = new UpdateUserSeasonQuery(user.getId(), season.getId(), user.getDoubleXp(), levelModel.getLevel(), levelModel.getXp());
        dbQueue.addStatement(query);
    }

    private void addInsertQuestUserSeason(QuestUserSeasonModel user) {
        UpdateUserSeasonQuery query = new UpdateUserSeasonQuery(user.getFuid(), season.getId(), user.getDoubleXp(), user.getLevel(), user.getXp(), true);
        dbQueue.addStatement(query);
    }

    private int calculateTotalXp(int level, int xp) {
        int totalXp = 0;
        HashMap<Integer, Integer> levels = QuestController.getInstance().getLevelXps();

        for (Integer levelId : levels.keySet()) {
            if (levelId < level) {
                totalXp += getLevelGoal(levelId);
            }
        }

        totalXp += xp;

        return totalXp;
    }

    private UserQuestLevelInfoModel getUserPreviousLevel(String fuid) {
        UserQuestLevelInfoModel previousUserLevel = null;

        if (season.getId() > 1) {
            QuestUserSeasonModel questUserSeasonModel = db.getQuestUserSeason(fuid, season.getId() - 1);

            if (questUserSeasonModel == null) previousUserLevel = new UserQuestLevelInfoModel(0, 1, getLevelGoal(1), 0);
            else {
                int level = questUserSeasonModel.getLevel();
                int xp = questUserSeasonModel.getXp();
                previousUserLevel = new UserQuestLevelInfoModel(xp, level, getLevelGoal(level), calculateTotalXp(level, xp));
            }
        }

        return previousUserLevel;
    }

    public void setSeason(QuestSeasonModel season) {
        this.season = season;
    }

    public void updateUserDifferentDaysPlay(QuestUser user){
        user.setLastPlayDay(season.getDay());
        cacheController.setUserDifferentDaysPlay(user.getId(), getSeason().getDay(), getSeason().getId(), getSeasonExpire());
    }

    public void updateUserDifferentDaysLogin(QuestUser user){
        user.setLastLoginDay(season.getDay());
        cacheController.setUserDifferentDaysLogin(user.getId(), getSeason().getDay(), getSeason().getId(), getSeasonExpire());
    }

    public void checkDifferentDaysLogin(String fuid){
        QuestCategoryInfo differentDaysCategoryInfo = new DifferentDaysCategoryInfo(fuid, DifferentDaysType.LOGIN.getValue());
        RuleSet ruleSet = getQuestRulesMap().get(QuestCategory.DIFFERENT_DAYS);
        ruleSet.checkRules(getGson().toJson(differentDaysCategoryInfo));
    }

    public boolean invalidQuestDay(QuestModel model) {
        int day = season.getDay();

        return (model.isDaily() && model.getDay() != day) || (!model.isDaily() && (day < 8 && model.getDay() >= 8));
    }


    public void clearUserWinningStreak(String id) {
        cacheController.clearWinningStreak(id, getSeason().getId());
    }

    public int incrementUserWinningStreak(QuestUser user) {
        int streak = user.getWinningStreak() + 1;
        user.setWinningStreak(streak);
        cacheController.setWinningStreak(user.getId(),season.getId(),getSeasonExpire(),streak);

        return streak;
    }

    public boolean isProxyConnected() {
        return connectedProxyCount > 0;
    }

    public void sendSeasonFinishedMessage(QuestUser user, String fuid) {
        String platform = cacheController.getUserLastPlatform(fuid);
        int tableId = cacheController.getTableId(fuid);

        //TODO:REMOVE platform == null after test
        if ((platform == null || (platform != null && !platform.equals("web"))) && tableId == -1) {

            QuestUserInfoMessage questUserInfoMessage = QuestController.getInstance().getQuestUserInfo(user, true);

            NetworkMessage response = new NetworkMessage();
            response.setDataAsJSON(questUserInfoMessage);
            response.setCmd(GameCommands.QUEST_USER_INFO);
            QuestController.getInstance().sendMessage(response, user);

        }
    }

    public void handleSeasonFinished() {
        logger.error("Season Finished Sent");

        for (QuestUser user : users.values()) {

            boolean differentSeason = (user.getSeasonId() != getSeason().getId());

            if (differentSeason) {
                String fuid = user.getId();
                QuestController.getInstance().setUser(user, true);

                sendSeasonFinishedMessage(user, fuid);
            }else if((user.isFirstTime())){
                String fuid = user.getId();

                sendSeasonFinishedMessage(user, fuid);
            }
        }
    }

    private void addRedisEventListeners() {
        CacheController.getInstance().listenUpdateSeasonEvent((channel, msg) -> {
            new Thread(() -> {
                try {
                    season = cacheController.getSeason();

                    logger.error("New Season:" + season.getId());
                    handleSeasonFinished();
                } catch (Exception e) {
                    System.err.println(e);
                }
            }).start();

        });
    }

    public boolean isRabbitConnected() {
        return rabbitConsumerThread.isRabbitConnected();
    }

    public void incrementProxyCount(){
        connectedProxyCount++;
    }

    public void decrementProxyCount(){
        connectedProxyCount--;
    }
}
