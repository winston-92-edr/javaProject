package com.mynet.questservice.quests.category;

public class TournamentCategoryInfo extends QuestCategoryInfo{
    private int id;
    private int playerCount;
    private int level;
    private boolean isTemp;
    private boolean isSucceed;

    public TournamentCategoryInfo(String fuid, int id, int playerCount, int level, boolean isTemp, boolean isSucceed) {
        this.fuid = fuid;
        this.id = id;
        this.playerCount = playerCount;
        this.level = level;
        this.isTemp = isTemp;
        this.isSucceed = isSucceed;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPlayerCount() {
        return playerCount;
    }

    public void setPlayerCount(int playerCount) {
        this.playerCount = playerCount;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public boolean isTemp() {
        return isTemp;
    }

    public void setTemp(boolean temp) {
        isTemp = temp;
    }

    public boolean isSucceed() {
        return isSucceed;
    }

}
