package com.mynet.questservice.quests.category;

public class DifferentDaysCategoryInfo extends QuestCategoryInfo {
    private int type;
    private long date;

    public DifferentDaysCategoryInfo(String fuid, int differentDaysTypelue) {
        this.fuid = fuid;
        this.type = differentDaysTypelue;
        this.date = System.currentTimeMillis();
    }
    public int getType() {
        return type;
    }

    public long getDate() {
        return date;
    }
}
