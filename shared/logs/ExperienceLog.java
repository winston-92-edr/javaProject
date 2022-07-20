package com.mynet.shared.logs;

public class ExperienceLog extends QueueElement{
    long fuid;
    long date;
    int amount;
    int xp;
    int questId;
    int dayInfo;
    int type;
    int season;
    String platform;

    public ExperienceLog(long fuid, int amount, int xp, int questId, int dayInfo, int type, int season, String platform) {
        this.fuid = fuid;
        this.amount = amount;
        this.xp = xp;
        this.questId = questId;
        this.dayInfo = dayInfo;
        this.type = type;
        this.season = season;
        this.platform = platform;
        this.date = System.currentTimeMillis();
    }
}
