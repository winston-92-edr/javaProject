package com.mynet.questservice.quests.models;

public class QuestInfoModel {
    int id;
    String description;
    int status; //completed,active,next
    int categoryType;
    int xp;
    int point;
    int goal;
    long ttl;
    boolean daily;

    public QuestInfoModel(int id, String description, int status, int categoryType, int xp, long ttl, int point, int goal, boolean daily) {
        this.id = id;
        this.description = description;
        this.status = status;
        this.categoryType = categoryType;
        this.xp = xp;
        this.ttl = ttl;
        this.point = point;
        this.goal = goal;
        this.daily = daily;
    }

    public int getStatus() {
        return status;
    }

    public int getXp() {
        return xp;
    }

    public String getDescription() {
        return description;
    }

    public int getCategoryType() {
        return categoryType;
    }

    public int getPoint() {
        return point;
    }

    public int getGoal() {
        return goal;
    }

    public long getTtl() {
        return ttl;
    }

    public boolean isDaily() {
        return daily;
    }

    public double getCompletion() {
        return ((double)getPoint()/(double)getGoal())*100;
    }

    @Override
    public String toString() {
        return "QuestInfoModel{" +
                "id=" + id +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", categoryType=" + categoryType +
                ", xp=" + xp +
                ", point=" + point +
                ", goal=" + goal +
                ", ttl=" + ttl +
                ", daily=" + daily +
                ", completion=" + getCompletion() +
                '}';
    }
}
