package com.mynet.shared.logs;

import com.mynet.gameserver.logs.GameData;
import com.mynet.questservice.quests.category.QuestCategory;
import com.mynet.questservice.quests.category.QuestCategoryInfo;
import com.mynet.shared.analytics.model.BaseEvent;
import com.mynet.shared.config.ServerConfiguration;
import com.mynet.shared.types.DataSourceType;
import com.mynet.shared.user.User;

public class RabbitMQLogController {
    private static final String logQueueHost = ServerConfiguration.get("RabbitMqHost");
    private static final String logQueueUsername = ServerConfiguration.get("RabbitMqUsername");
    private static final String logQueuePass = ServerConfiguration.get("RabbitMqPass");
    private RabbitMQThread winnerLogDbThread;
    private RabbitMQThread tournamentTableLogThread;
    private RabbitMQThread gameDataLogThread;
    private RabbitMQThread tournamentEventLogThread;
    private RabbitMQThread userQuestLogThread;
    private RabbitMQThread chatLogThread;
    private RabbitMQThread privateChatLogThread;
    private RabbitMQThread bonusLogThread;
    private RabbitMQThread analyticsThread;

    private static RabbitMQLogController instance;

    public RabbitMQLogController() {
        initWinnerLogDbThread();
        initTournamentTableLogThread();
        initGameDataLogThread();
        initTournamentEventLogThread();
        initUserQuestLogThread();
        initChatLogThread();
        initPrivateChatLogThread();
        initBonusLogThread();
        initAnalyticsLogThread();
    }

    public static void init() {

        if(instance == null){
            instance = new RabbitMQLogController();
        }

    }

    public static RabbitMQLogController getInstance(){
        return instance;
    }

    private void initWinnerLogDbThread() {
        String winnerLogQueueName = ServerConfiguration.get("RabbitMqWinnerLog");
        winnerLogDbThread = new RabbitMQThread(winnerLogQueueName, logQueueHost, logQueueUsername, logQueuePass);
    }

    private void initTournamentTableLogThread() {
        String eventLogQueueName = ServerConfiguration.get("RabbitMqTournamentTableLog");
        tournamentTableLogThread = new RabbitMQThread(eventLogQueueName, logQueueHost, logQueueUsername, logQueuePass);
    }

    private void initGameDataLogThread() {
        String gameDataLogQueueName = ServerConfiguration.get("RabbitMqGameDataLog");
        gameDataLogThread = new RabbitMQThread(gameDataLogQueueName, logQueueHost, logQueueUsername, logQueuePass);

    }

    private void initTournamentEventLogThread() {
        String tournamentEventLog = ServerConfiguration.get("RabbitMqTournamentEventLog");
        tournamentEventLogThread = new RabbitMQThread(tournamentEventLog, logQueueHost, logQueueUsername, logQueuePass);

    }

    private void initChatLogThread() {
        String eventLogQueueName = ServerConfiguration.get("RabbitMqChatLog");

        this.chatLogThread = new RabbitMQThread(eventLogQueueName, logQueueHost, logQueueUsername, logQueuePass);
    }

    private void initPrivateChatLogThread() {
        String eventLogQueueName = ServerConfiguration.get("RabbitMqPrivateChatLog");

        this.privateChatLogThread = new RabbitMQThread(eventLogQueueName, logQueueHost, logQueueUsername, logQueuePass);
    }

    //Dont forget to add queue name to conf
    private void initUserQuestLogThread(){
        String QuestLogQueueName = ServerConfiguration.get("RabbitMqQuestLog");

        this.userQuestLogThread = new RabbitMQThread(QuestLogQueueName, logQueueHost, logQueueUsername, logQueuePass);
    }

    private void initBonusLogThread() {
        String bonusLogThreadName = ServerConfiguration.get("RabbitMqBonusLog");
        bonusLogThread = new RabbitMQThread(bonusLogThreadName, logQueueHost, logQueueUsername, logQueuePass);
    }

    private void initAnalyticsLogThread() {
        String analyticsLogThreadName = ServerConfiguration.get("RabbitMqAnalyticsLog");
        analyticsThread = new RabbitMQThread(analyticsLogThreadName, logQueueHost, logQueueUsername, logQueuePass);
    }

    public void addWinnerLogDbLog(WinnerLogDbData winnerLogDbData) {
        winnerLogDbThread.addQuery(winnerLogDbData);
    }

    public void winnerLog(User winner, User side1, User side2, User side3, long table_id, long bet, long canak,
                                 String game_id, int game_type, int room_type, Long how_long, String finished_hands, String okey, String last_hand, int partner_side, String pot_users, int sideCount,
                                 int game_mode, long podId, boolean isPotWin, int table_type) throws Exception {

        StringBuilder sb = new StringBuilder();
        sb.append(winner.getId());
        sb.append(";");
        sb.append(winner.getIp());
        sb.append(";");
        sb.append((side1 != null) ? side1.getId() : "-1");
        sb.append(";");
        sb.append((side1 != null) ? side1.getIp() : "bot 1");
        sb.append(";");
        sb.append((side2 != null) ? side2.getId() : "-2");
        sb.append(";");
        sb.append((side2 != null) ? side2.getIp() : "bot 2");
        sb.append(";");
        sb.append((side3 != null) ? side3.getId() : "-3");
        sb.append(";");
        sb.append((side3 != null) ? side3.getIp() : "bot 3");
        sb.append(";");
        sb.append(winner.getGameNodeId());
        sb.append(";");
        sb.append(table_id);
        sb.append(";");
        sb.append(bet * sideCount);
        sb.append(";");
        sb.append(canak);
        sb.append(";");
        sb.append(game_id);
        sb.append(";");
        sb.append(game_type);
        sb.append(";");
        sb.append(room_type);
        sb.append(";");
        sb.append(how_long);
        sb.append(";");
        sb.append(finished_hands);
        sb.append(";");
        sb.append(okey);
        sb.append(";");
        sb.append(last_hand);
        sb.append(";");
        sb.append(partner_side + ".");
        sb.append(";");
        sb.append(pot_users + ".");
        sb.append(";");
        sb.append(game_mode);
        sb.append(";");
        sb.append(podId);
        sb.append(";");
        sb.append(isPotWin);
        sb.append(";");
        sb.append(table_type);
        sb.append(";");
        sb.append(sideCount);

        String parameters = sb.toString();
        addWinnerLogDbLog(new WinnerLogDbData(WinnerDbLogType.LOG_WINNERS, parameters, DataSourceType.WINNER_LOGS));
    }

    public void addTournamentTableLog(TournamentTableLog tournamentTableLog) {
        tournamentTableLogThread.addQuery(tournamentTableLog);
    }

    public void addGameDataLog(GameData gameData) {
        gameDataLogThread.addQuery(gameData);
    }

    public void addTournamentEventLog(TournamentEventLog tournamentEventLog) {
        tournamentEventLogThread.addQuery(tournamentEventLog);
    }

    public void addUserQuestLog(QuestCategory questCategory, QuestCategoryInfo questCategoryInfo) {
        //this.userQuestLogThread.addQuery(new UserQuestThreadLog(questCategory,questCategoryInfo));
    }

    public void addChatLog(ChatThreadLog log) {
        this.chatLogThread.addQuery(log);
    }

    public void addPrivateChatLog(PrivateChatThreadLog log) {this.privateChatLogThread.addQuery(log);}

    public void addBonusLog(BonusLog log) {this.bonusLogThread.addQuery(log);}

    public void addAnalyticsLog(BaseEvent log) {
        this.analyticsThread.addQuery(log);
    }
}
