package com.mynet.questservice.quests.ruleSets;

import com.mynet.questservice.quests.rules.*;
import com.mynet.questservice.quests.types.QuestType;

public class WonGameRuleSet extends RuleSet {

    @Override
    public void registerRules() {
        rules.put(QuestType.WON_GAME, new GameRule());
        rules.put(QuestType.WON_CLASSIC_GAME, new WonClassicGameRule());
        rules.put(QuestType.WON_GAME_WITH_OKEY, new WonGameOkeyRule());
        rules.put(QuestType.BREAK_POT, new BreakPotGameRule());
        rules.put(QuestType.WON_GAME_WITH_DOUBLE,new WonGameDoubleRule());
        rules.put(QuestType.WON_GAME_WITH_LONG_TILE, new WonGameLongestTileRule());
        rules.put(QuestType.WON_SINGLE_GAME, new WonSingleGameRule());
        rules.put(QuestType.WON_PAIRED_GAME, new WonPairedGameRule());
        rules.put(QuestType.WON_PAIRED_GAME_GAIN_XP, new WonPairedGameGainXpRule());
        rules.put(QuestType.WON_SINGLE_GAME_GAIN_XP, new WonSingleGameGainXpRule());
        rules.put(QuestType.WON_GAME_WITH_FAKE_OKEY, new WonGameFakeOkeyRule());
    }

}
