package com.mynet.questservice.quests.models;

public class QuestInfoAwardModel{
    private final int id;
    private final int xp;
    private final int level;
    private final int award;
    private final int status;
    private final String title;
    private final String type;
    private final boolean pass;
    private final int season;
    private final String imageName;

    public QuestInfoAwardModel(int id, String title, boolean pass, String type, int xp, int level, int award, int status, int season, String imageName) {
        this.id = id;
        this.title = title;
        this.pass = pass;
        this.type = type;
        this.xp = xp;
        this.level = level;
        this.award = award;
        this.status = status;
        this.season = season;
        this.imageName = imageName;
    }

    @Override
    public String toString() {
        return "QuestInfoAwardModel{" +
                "id=" + id +
                ", xp=" + xp +
                ", level=" + level +
                ", award=" + award +
                ", status=" + status +
                ", title='" + title + '\'' +
                ", type='" + type + '\'' +
                ", pass=" + pass +
                ", season=" + season +
                ", imageName=" + imageName +
                '}';
    }

    public QuestInfoAwardModel(QuestAwardModel award, int status, int season) {
        this.id = award.getId();
        this.title = award.getTitle();
        this.pass = award.isPass();
        this.type = award.getType().getValue();
        this.xp = award.getXp();
        this.level = award.getLevel();
        this.award = award.getAward();
        this.status = status;
        this.season =  season;
        this.imageName = award.getImageName();
    }

    public int getId() {
        return id;
    }

    public int getXp() {
        return xp;
    }

    public int getLevel() {
        return level;
    }

    public int getAward() {
        return award;
    }

    public int getStatus() {
        return status;
    }

    public String getTitle() {
        return title;
    }

    public String getType() {
        return type;
    }

    public boolean isPass() {
        return pass;
    }
}
