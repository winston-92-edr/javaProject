package com.mynet.questservice.quests.models;

import com.mynet.questservice.quests.types.QuestType;

import java.beans.ConstructorProperties;

public class QuestModel {
    private final int id;
    private final int xp;
    private final String title;
    private final String description;
    private final QuestType type;
    private final boolean daily;
    private final int day;
    private final int premise;
    private final int goal;

    @ConstructorProperties({"id", "xp", "title", "description", "type", "daily", "day", "premise", "goal"})
    public QuestModel(int id, int xp, String title, String description, int type, boolean daily, int day, int premise, int goal) {
        this.id = id;
        this.xp = xp;
        this.title = title;
        this.description = description;
        this.type = QuestType.forCode(type);
        this.daily = daily;
        this.day = day;
        this.premise = premise;
        this.goal = goal;
    }

    public int getId() {
        return id;
    }

    public int getXp() {
        return xp;
    }

    public String getTitle() {
        return title;
    }

    public QuestType getType() {
        return type;
    }

    public boolean isDaily() {
        return daily;
    }

    public int getDay() {
        return day;
    }

    public int getPremise() {
        return premise;
    }

    public int getGoal() {
        return goal;
    }

    public String getDescription() {
        return description;
    }

}
