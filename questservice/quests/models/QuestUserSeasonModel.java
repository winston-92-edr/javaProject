package com.mynet.questservice.quests.models;

import java.beans.ConstructorProperties;

public class QuestUserSeasonModel {
    private String fuid;
    private int doubleXp;
    private int level;
    private int season;
    private int xp;

    @ConstructorProperties({"fuid", "doubleXp", "level", "season", "xp"})
    public QuestUserSeasonModel(String fuid, int doubleXp, int level, int season, int xp) {
        this.fuid = fuid;
        this.doubleXp = doubleXp;
        this.level = level;
        this.season = season;
        this.xp = xp;
    }

    public String getFuid() {
        return fuid;
    }

    public int getDoubleXp() {
        return doubleXp;
    }

    public int getLevel() {
        return level;
    }

    public int getSeason() {
        return season;
    }

    public int getXp() {
        return xp;
    }
}
