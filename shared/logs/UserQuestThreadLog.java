package com.mynet.shared.logs;
import com.mynet.questservice.quests.category.QuestCategory;
import com.mynet.questservice.quests.category.QuestCategoryInfo;

public class UserQuestThreadLog extends QueueElement{
    QuestCategory category;
    QuestCategoryInfo info;

    public UserQuestThreadLog(QuestCategory category, QuestCategoryInfo info) {
        this.category = category;
        this.info = info;
    }

    public UserQuestThreadLog() {
    }

    public void setCategory(QuestCategory category) {
        this.category = category;
    }

    public void setInfo(QuestCategoryInfo info) {
        this.info = info;
    }

    public QuestCategory getCategory() {
        return category;
    }

    public QuestCategoryInfo getInfo() {
        return info;
    }

    @Override
    public String toString() {
        return "UserQuestThreadLog{" +
                "category=" + category +
                ", info=" + info +
                '}';
    }
}