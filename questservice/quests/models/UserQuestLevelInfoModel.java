package com.mynet.questservice.quests.models;

public class UserQuestLevelInfoModel {
    private int xp;
    private int level;
    private int totalXp; //Level goal
    private int overallXp;

    public UserQuestLevelInfoModel(int xp, int level, int totalXp, int overallXp) {
        this.xp = xp;
        this.level = level;
        this.totalXp = totalXp;
        this.overallXp = overallXp;
    }

    public UserQuestLevelInfoModel(UserQuestLevelModel model, int goal) {
        this.xp = model.getXp();
        this.level = model.getLevel();
        this.overallXp = model.getTotalXp();
        this.totalXp = goal;
    }

    public int getLevel() {
        return level;
    }
}
