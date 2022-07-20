package com.mynet.questservice.quests.messages;

import com.mynet.questservice.quests.models.UserPreviousSeasonModel;
import com.mynet.questservice.quests.models.UserQuestLevelInfoModel;

public class QuestUserInfoMessage {
    private final UserQuestLevelInfoModel userLevel;
    private final long seasonTtl;
    private final boolean availableAward;
    private final UserPreviousSeasonModel userPreviousSeason;
    private final boolean seasonHandShake;
    private final boolean seasonFinished;

    public QuestUserInfoMessage(UserQuestLevelInfoModel userLevel, long seasonTtl, boolean availableAward, UserPreviousSeasonModel userPreviousSeason, boolean seasonHandShake, boolean seasonFinished) {
        this.userLevel = userLevel;
        this.seasonTtl = seasonTtl;
        this.availableAward = availableAward;
        this.userPreviousSeason = userPreviousSeason;
        this.seasonHandShake = seasonHandShake;
        this.seasonFinished = seasonFinished;
    }

    @Override
    public String toString() {
        return "QuestUserInfoMessage{" +
                "userLevel=" + userLevel +
                ", seasonTtl=" + seasonTtl +
                ", availableAward=" + availableAward +
                ", userPreviousSeason=" + userPreviousSeason +
                ", seasonHandShake=" + seasonHandShake +
                ", seasonFinished=" + seasonFinished +
                '}';
    }
}
