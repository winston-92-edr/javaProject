package com.mynet.questservice.quests.category;

public class GameActionCategoryInfo extends QuestCategoryInfo{
    private int gameActionType;

    public GameActionCategoryInfo(String fuid, int gameActionType) {
        this.fuid = fuid;
        this.gameActionType = gameActionType;
    }
    public int getGameActionType() {
        return gameActionType;
    }
}
