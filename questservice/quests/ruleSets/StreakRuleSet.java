package com.mynet.questservice.quests.ruleSets;

import com.mynet.questservice.quests.rules.WinningStreakRule;
import com.mynet.questservice.quests.types.QuestType;

public class StreakRuleSet extends RuleSet{
    @Override
    public void registerRules() {
        rules.put(QuestType.WINNING_STREAK,new WinningStreakRule());
    }
}
