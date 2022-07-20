package com.mynet.questservice.quests.models;

import com.mynet.questservice.quests.types.QuestAwardType;

import java.beans.ConstructorProperties;

public class QuestAwardModel {
    private final int id;
    private final String title;
    private final String description;
    private final boolean pass;
    private final QuestAwardType type;
    private final int xp;
    private final int level;
    private final int award;
    private final String imageName;

    @ConstructorProperties({"id", "title", "description", "pass", "type", "xp", "level", "award", "imageName"})
    public QuestAwardModel(int id, String title, String description, boolean pass, String type, int xp, int level, int award, String imageName) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.pass = pass;
        this.type = QuestAwardType.forCode(type);
        this.xp = xp;
        this.level = level;
        this.award = award;
        this.imageName = imageName;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public boolean isPass() {
        return pass;
    }

    public QuestAwardType getType() {
        return type;
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

    public String getImageName() {
        return imageName;
    }
}
