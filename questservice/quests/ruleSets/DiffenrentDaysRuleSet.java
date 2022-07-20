package com.mynet.questservice.quests.ruleSets;

import com.mynet.questservice.quests.rules.DifferentDaysLoginRule;
import com.mynet.questservice.quests.rules.DifferentDaysPlayRule;
import com.mynet.questservice.quests.types.QuestType;

public class DiffenrentDaysRuleSet extends RuleSet{
    @Override
    public void registerRules() {
            rules.put(QuestType.DIFFERENT_DAYS_PLAY,new DifferentDaysPlayRule());
            rules.put(QuestType.DIFFERENT_DAYS_LOGIN,new DifferentDaysLoginRule());
    }
}
