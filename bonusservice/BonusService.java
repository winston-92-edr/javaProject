package com.mynet.bonusservice;

import com.mynet.bonusservice.model.BonusModel;
import com.mynet.bonusservice.model.UserBonusInfo;
import com.mynet.bonusservice.model.rules.BonusRule;
import com.mynet.gameserver.enums.InfoCode;
import com.mynet.proxyserver.user.UserModel;
import com.mynet.shared.config.ServerConfiguration;
import com.mynet.shared.connection.NodeToProxy;
import com.mynet.shared.logs.BonusLog;
import com.mynet.shared.network.GameCommands;
import com.mynet.shared.network.NetworkMessage;
import com.mynet.shared.resource.CacheController;
import com.mynet.shared.resource.db.DBController;
import com.mynet.shared.response.InfoResponse;
import com.mynet.shared.user.ProxyUser;
import com.mynet.shared.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.naming.CannotProceedException;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class BonusService {
    Logger logger = LoggerFactory.getLogger(BonusService.class);
    List<BonusModel> bonusList;
    private ConcurrentHashMap<String, ProxyUser> users;
    private NodeToProxy nodeToProxy;
    private BonusServiceQueue bonusServiceQueue;
    private RabbitConsumerThread rabbitConsumerThread;
    private boolean readyForConsume;

    private static BonusService instance;

    private final CacheController cacheController;

    public static void init(CacheController cacheController) {
        if (instance == null) {
            instance = new BonusService(cacheController);
        }
    }

    public static BonusService getInstance() {
        return instance;
    }

    public BonusService(CacheController cacheController) {
        this.cacheController = cacheController;
        this.bonusList = DBController.getInstance().getBonuses();

        this.users = new ConcurrentHashMap<>();
        this.nodeToProxy = new NodeToProxy();

        this.bonusServiceQueue = new BonusServiceQueue(this);
        setConsumer();
        controlConsumer(true);
        addRedisListeners();

        logger.info("Bonus Service initialize...");
    }

    public ProxyUser createUser(String userID) throws CannotProceedException {

        if (hasUser(userID)) {
            users.remove(userID);
        }

        UserModel userModel = new UserModel(userID);

        ProxyUser user = new ProxyUser(userModel);

        HashMap<Integer, UserBonusInfo> bonusMap = DBController.getInstance().getUserBonuses(userID);
        user.setBonusMap(bonusMap);

        UserModel cachedUser = cacheController.getUserGameModel(userID);
        user.setProxyId(cachedUser.proxyID);
        user.setAnalyticsSessionId(cachedUser.analyticsSessionId);
        user.setAnalyticsDeviceId(cachedUser.analyticsDeviceId);
        user.setApplicationVersion(cachedUser.applicationVersion);

        logger.info(String.format("%s created", userID));
        addUser(user);

        return user;
    }

    public boolean addUser(ProxyUser user) {
        String userID = user.getId();

        if (users.containsKey(userID)) {
            return false;
        }

        users.put(userID, user);

        return true;
    }

    public void removeUser(String id) {
        if (hasUser(id)) {
            users.remove(id);
        }
    }


    boolean hasUser(String id) {
        return users.containsKey(id);
    }

    public void resetUser(String userID) {
        if (!hasUser(userID)) {
            try {
                createUser(userID);
            } catch (CannotProceedException e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

    public ProxyUser getUser(String id) {
        return users.get(id);
    }

    public void checkBonus(BonusLog log) {

        try {

            List<BonusModel> availableBonusList = bonusList.stream().filter(x -> x.getRuleTypes().contains(log.getType())).collect(Collectors.toList());

            ProxyUser user = getUser(log.getId());

            if(user != null) {
                for (BonusModel bonus : availableBonusList) {

                    logger.info(String.format("Bonus id: %d check for user: %s", bonus.getId(), log.getId()));

                    if (bonus.getEndDate() != -1 && System.currentTimeMillis() >= bonus.getEndDate()) continue;

                    UserBonusInfo info = user.getBonus(bonus.getId());
                    if (info != null && bonus.getCount() != -1 && info.getCount() >= bonus.getCount()) continue;

                    String userId = user.getId();
                    if (bonus.getRepeatInterval() != -1 && !CacheController.getInstance().getBonusAvailable(userId, bonus.getId())) continue;

                    boolean success = true;

                    for (BonusRule rule : bonus.getRules()) {
                        if (!rule.isValid(log)) {
                            success = false;
                            break;
                        }
                    }

                    logger.info(String.format("Bonus id: %d is %b for user: %s", bonus.getId(), success, log.getId()));

                    if (!success) continue;

                    if (info == null) {
                        info = new UserBonusInfo(bonus.getId());
                        user.addBonus(info.getBonusId(), info);
                    } else {
                        user.increaseBonusCount(info.getBonusId());
                    }

                    boolean recordExist = DBController.getInstance().checkUserBonus(userId, info);

                    if (recordExist) {
                        DBController.getInstance().updateUserBonus(userId, info);
                    } else {
                        DBController.getInstance().addUserBonus(userId, info);
                    }

                    if (bonus.getRepeatInterval() != -1) {
                        cacheController.setBonusKey(userId, bonus.getId(), bonus.getRepeatInterval());
                    }

                    int triggerTime = bonus.getTriggerTime();

                    if (triggerTime == 0) giveAward(user, bonus);
                    else Utils.setTimeout(() -> giveAward(user, bonus), triggerTime);
                }
            }
        } catch (Exception e) {
            logger.error("Error at check bonus:" + e.getMessage());
        }
    }

    private void giveAward(ProxyUser user, BonusModel bonus) {

        try {

            if (bonus.getMl() != 0) {
                user.updateMoneyAndWriteLog(bonus.getName(), -1, bonus.getMl(), true, System.currentTimeMillis());

                cacheController.publishUpdateMoney(user.getId());
            }

            if (bonus.getTicket() != 0) {
                boolean updateSuccess = DBController.getInstance().updateUserTickets(user.getId(), bonus.getTicket(), true);

                if (updateSuccess) {
                    user.increaseTickets(bonus.getTicket());

                    user.writeUserTicketLog(bonus.getName(), -1, bonus.getTicket(), System.currentTimeMillis());

                    cacheController.publishUpdateTicket(user.getId());
                }
            }

            if (bonus.getMessage() != null && user != null) {
                NetworkMessage resp = new NetworkMessage(GameCommands.INFO);
                resp.setDataAsJSON(new InfoResponse(bonus.getMessage(), InfoCode.DEFAULT));
                nodeToProxy.addServerMessage(resp, user);
            }

        } catch (Exception e) {
            logger.error("Error at give bonus award:" + e.getMessage());
        }
    }

    public void addMessage(String message){
        try {
            BonusLog log = NetworkMessage.CreateMessage(message, BonusLog.class);
            this.bonusServiceQueue.add(log);
        }catch (Exception e){
            logger.info(e.getMessage());
        }
    }

    private void addRedisListeners() {

        cacheController.listenUpdateBonusesEvent((charSequence, info) -> {
            bonusList = DBController.getInstance().getBonuses();
            logger.info("Bonuses updated!");
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

    public void controlConsumer(boolean ready) {
        if (ready) {
            readyForConsume = true;
            rabbitConsumerThread.start();
            System.out.println("Bonus consumer started");
        } else {
            readyForConsume = false;
            rabbitConsumerThread.stop();
            System.out.println("Bonus consumer stopped.");
        }
    }

    private void setConsumer() {
        String host = ServerConfiguration.get("RabbitMqHost");
        String username = ServerConfiguration.get("RabbitMqUsername");
        String pass = ServerConfiguration.get("RabbitMqPass");
        String queue = ServerConfiguration.get("RabbitMqBonusLog");
        rabbitConsumerThread = new RabbitConsumerThread(queue, host, username, pass);
    }

    public boolean isReadyForConsume() {
        return readyForConsume;
    }
}
