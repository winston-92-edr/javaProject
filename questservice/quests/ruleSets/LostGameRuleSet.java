package com.mynet.questservice.quests.ruleSets;

import com.mynet.questservice.quests.rules.LostPairedGameGainXpRule;
import com.mynet.questservice.quests.rules.LostSingleGameGainXpRule;
import com.mynet.questservice.quests.types.QuestType;

public class LostGameRuleSet extends RuleSet{

    @Override
    public void registerRules() {
        rules.put(QuestType.LOST_SINGLE_GAME_GAIN_XP, new LostSingleGameGainXpRule());
        rules.put(QuestType.LOST_PAIRED_GAME_GAIN_XP, new LostPairedGameGainXpRule());
    }
}
