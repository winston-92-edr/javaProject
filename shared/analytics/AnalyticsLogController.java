package com.mynet.shared.analytics;

import com.mynet.shared.analytics.enums.EventType;
import com.mynet.shared.analytics.model.GameEconomy;
import com.mynet.shared.analytics.model.GameLog;
import com.mynet.shared.analytics.model.SpecialEvent;
import com.mynet.shared.logs.RabbitMQLogController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.*;

public class AnalyticsLogController {
    private static AnalyticsLogController instance;
    private static Logger logger = LoggerFactory.getLogger(AnalyticsLogController.class);

    public static void init() {
        if(instance == null){
            instance = new AnalyticsLogController();
        }

    }

    public static AnalyticsLogController getInstance(){
        return instance;
    }

    public void sendGameEconomyLog(String userId, String devId, String sessionId, Map<String, Object> gameData, long balance, float transaction, String transactionType, String source, String platform, String appVersion){
        GameEconomy log = new GameEconomy(userId, devId, sessionId, EventType.GAME_ECONOMY, gameData, balance, transaction,transactionType, source, platform, appVersion);
        RabbitMQLogController.getInstance().addAnalyticsLog(log);
    }

    public void sendGameLog(String userId, String deviceId, String sessionId, Map<String, Object> gameData, String gameId, String gameType, boolean logType, int gameResult, int tableType, String platform, String appVersion){
        GameLog log = new GameLog(userId,deviceId,sessionId,EventType.GAME_LOG, gameData, gameId,gameType,logType, gameResult,tableType, platform, appVersion);
        RabbitMQLogController.getInstance().addAnalyticsLog(log);
    }

    public void sendSpecialEvent(String userId, String deviceId, String sessionId, Map<String, Object> gameData, String specialEventName, String stage1, String stage2, String stage3, String platform, String appVersion){
        SpecialEvent log = new SpecialEvent(userId, deviceId, sessionId, EventType.SPECIAL_EVENT, gameData, specialEventName, stage1, stage2, stage3, platform, appVersion);
        RabbitMQLogController.getInstance().addAnalyticsLog(log);
    }
}
