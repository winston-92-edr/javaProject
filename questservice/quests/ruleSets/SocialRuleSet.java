package com.mynet.questservice.quests.ruleSets;

import com.mynet.questservice.quests.rules.GetFriendRule;
import com.mynet.questservice.quests.rules.InviteUserRule;
import com.mynet.questservice.quests.rules.SendTableMessageRule;
import com.mynet.questservice.quests.types.QuestType;

public class SocialRuleSet extends RuleSet {
    @Override
    public void registerRules() {
        rules.put(QuestType.INVITE_USER, new InviteUserRule());
        rules.put(QuestType.GET_FRIEND, new GetFriendRule());
        rules.put(QuestType.SEND_MESSAGE_TO_TABLE, new SendTableMessageRule());
    }
}
