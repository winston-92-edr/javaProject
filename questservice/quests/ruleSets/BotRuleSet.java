package com.mynet.questservice.quests.ruleSets;

import com.mynet.questservice.quests.rules.BotRule;
import com.mynet.questservice.quests.types.QuestType;

public class BotRuleSet extends RuleSet{
    @Override
    public void registerRules() {
        rules.put(QuestType.FINISH_GAME_FOR_BOT, new BotRule());
    }
}
