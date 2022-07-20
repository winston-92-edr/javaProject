package com.mynet.questservice.quests.category;

public class StreakCategoryInfo extends QuestCategoryInfo{
    private int type;
    private boolean clear;

    public StreakCategoryInfo(String fuid, int type, boolean clear) {
        this.fuid = fuid;
        this.type = type;
        this.clear = clear;
    }

    public int getType() {
        return type;
    }

    public boolean isClear() {
        return clear;
    }
}
