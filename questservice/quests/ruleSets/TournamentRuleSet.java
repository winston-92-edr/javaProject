package com.mynet.questservice.quests.ruleSets;

import com.mynet.questservice.quests.rules.LevelTournamentRule;
import com.mynet.questservice.quests.rules.WonTournamentGameGainXpRule;
import com.mynet.questservice.quests.types.QuestType;

public class TournamentRuleSet extends RuleSet{
    @Override
    public void registerRules() {
        rules.put(QuestType.LEVEL_AT_CLASSIC, new LevelTournamentRule());
        rules.put(QuestType.LEVEL_AT_DUEL, new LevelTournamentRule());
        rules.put(QuestType.WON_TOURNAMENT_GAME_GAIN_XP, new WonTournamentGameGainXpRule());
    }
}
