package com.mynet.questservice.quests.messages;

import com.mynet.questservice.quests.models.UserQuestLevelInfoModel;

public class UpdateXpMessage {
    private final UserQuestLevelInfoModel oldLevel;
    private final UserQuestLevelInfoModel newLevel;
    private final boolean levelUp;
    private final long ttl;

    public UpdateXpMessage(UserQuestLevelInfoModel oldLevel, UserQuestLevelInfoModel newLevel, boolean levelUp, long ttl) {
        this.oldLevel = oldLevel;
        this.newLevel = newLevel;
        this.levelUp = levelUp;
        this.ttl = ttl;
    }
}
