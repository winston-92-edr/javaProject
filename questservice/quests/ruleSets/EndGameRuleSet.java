package com.mynet.questservice.quests.ruleSets;

import com.mynet.questservice.quests.rules.*;
import com.mynet.questservice.quests.types.QuestType;

public class EndGameRuleSet extends RuleSet {
    @Override
    public void registerRules() {
        rules.put(QuestType.FINISH_GAME, new GameRule());
        rules.put(QuestType.FINISH_PAIRED_GAME, new FinishPairedGameRule());
        rules.put(QuestType.FINISH_SINGLE_GAME, new FinishSingleGameRule());
        rules.put(QuestType.FINISH_GAME_AT_ROOM, new FinishGameAtRoomRule());
        rules.put(QuestType.FINISH_CLASSIC_GAME, new FinishClassicGameRule());
    }
}
