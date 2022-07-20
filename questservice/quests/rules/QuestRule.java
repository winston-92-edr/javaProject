package com.mynet.questservice.quests.rules;

import com.mynet.questservice.quests.models.QuestModel;

public interface QuestRule {
    public void check(QuestModel model, String info, int day);
}
