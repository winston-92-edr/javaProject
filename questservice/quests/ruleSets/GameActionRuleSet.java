package com.mynet.questservice.quests.ruleSets;

import com.mynet.questservice.quests.rules.GoDoubleRule;
import com.mynet.questservice.quests.types.QuestType;

public class GameActionRuleSet extends RuleSet{
    @Override
    public void registerRules() {
        rules.put(QuestType.GO_DOUBLE,new GoDoubleRule());
    }
}
