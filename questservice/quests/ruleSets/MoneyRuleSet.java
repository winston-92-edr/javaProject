package com.mynet.questservice.quests.ruleSets;

import com.mynet.questservice.quests.rules.ClassicEarnMoneyRule;
import com.mynet.questservice.quests.rules.EarnMoneyRule;
import com.mynet.questservice.quests.rules.PairedEarnMoneyRule;
import com.mynet.questservice.quests.rules.SingleEarnMoneyRule;
import com.mynet.questservice.quests.types.QuestType;

public class MoneyRuleSet extends RuleSet{
    @Override
    public void registerRules() {
        rules.put(QuestType.EARN_MONEY,new EarnMoneyRule());
        rules.put(QuestType.EARN_MONEY_PAIRED_GAME, new PairedEarnMoneyRule());
        rules.put(QuestType.EARN_MONEY_SINGLE_GAME, new SingleEarnMoneyRule());
        rules.put(QuestType.EARN_MONEY_CLASSIC,new ClassicEarnMoneyRule());
    }
}
