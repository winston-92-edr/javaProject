package com.mynet.questservice.quests.models;

import com.mynet.questservice.QuestController;

import java.util.concurrent.ConcurrentHashMap;

public class QuestUser {
    private String id;
    private String platform;
    private ConcurrentHashMap<Integer, UserQuestModel> quests;
    private UserQuestLevelModel questLevelModel;
    private long seasonPassEndDate;
    private int doubleXp;
    private int extraXpAmount;
    private int extraXpAward;
    private boolean firstTime;
    private UserPreviousSeasonModel userPreviousSeason;
    private int proxyId;
    private int lastPlayDay;
    private int winningStreak;
    private int lastLoginDay;
    private int seasonId;
    private int proxyID;

    public QuestUser(String id) {
        this.id = id;
    }

    public UserQuestModel getAndCreateQuest(int questId){
        UserQuestModel quest = quests.get(questId);
        QuestModel model = QuestController.getInstance().getQuest(questId);
        if(quest == null){
            quest = createQuest(model);
        }
        return quest;
    }

    public UserQuestModel getQuest(int questId){
        return quests.get(questId);
    }

    private void setQuest(UserQuestModel quest) {
        quests.put(quest.getQuestId(), quest);
    }

    public ConcurrentHashMap<Integer, UserQuestModel> getQuests(){
        return quests;
    }

    public void setQuests(ConcurrentHashMap<Integer, UserQuestModel> userQuests) {
        quests = userQuests;
    }

    public void setQuestLevelModel(UserQuestLevelModel questLevelModel) {
        this.questLevelModel = questLevelModel;
    }

    public UserQuestLevelModel getQuestLevelModel() {
        return questLevelModel;
    }

    public String getId() {
        return id;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public long getSeasonPassEndDate() {
        return seasonPassEndDate;
    }

    public void setSeasonPassEndDate(long seasonPassEndDate) {
        this.seasonPassEndDate = seasonPassEndDate;
    }


    private UserQuestModel createQuest(QuestModel model) {
        UserQuestModel quest = new UserQuestModel(model.getId(), 0);
        quest.setGoal(model.getGoal());
        setQuest(quest);

        QuestController.getInstance().addInsertUserQuestQuery(getId(), model.getId(), 0);

        return quest;
    }

    public int getDoubleXp() {
        return doubleXp;
    }

    public void setDoubleXp(int doubleXp) {
        this.doubleXp = doubleXp;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getExtraXpAmount() {
        return extraXpAmount;
    }

    public void setExtraXpAmount(int extraXpAmount) {
        this.extraXpAmount = extraXpAmount;
    }

    public int getExtraXpAward() {
        return extraXpAward;
    }

    public void setExtraXpAward(int extraXpAward) {
        this.extraXpAward = extraXpAward;
    }

    public void setFirstTime(boolean firstTime) {
        this.firstTime = firstTime;
    }

    public boolean isFirstTime() {
        return firstTime;
    }

    public UserPreviousSeasonModel getUserPreviousSeason() {
        return userPreviousSeason;
    }

    public void setUserPreviousSeason(UserPreviousSeasonModel userPreviousSeason) {
        this.userPreviousSeason = userPreviousSeason;
    }

    public int getProxyId() {
        return proxyId;
    }

    public void setProxyId(int proxyId) {
        this.proxyId = proxyId;
    }

    public int getLastPlayDay() {
        return lastPlayDay;
    }

    public void setLastPlayDay(int lastPlayDay) {
        this.lastPlayDay = lastPlayDay;
    }

    public int getWinningStreak() {
        return winningStreak;
    }

    public void setWinningStreak(int winningStreak) {
        this.winningStreak = winningStreak;
    }

    public int getLastLoginDay() {
        return lastLoginDay;
    }

    public void setLastLoginDay(int lastLoginDay) {
        this.lastLoginDay = lastLoginDay;
    }

    public int getSeasonId() {
        return seasonId;
    }

    public void setSeasonId(int seasonId) {
        this.seasonId = seasonId;
    }

    public int getProxyID() {
        return proxyID;
    }

    public void setProxyID(int proxyID) {
        this.proxyID = proxyID;
    }
}
