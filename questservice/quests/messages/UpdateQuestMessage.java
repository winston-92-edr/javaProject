package com.mynet.questservice.quests.messages;

import com.mynet.questservice.quests.models.QuestInfoModel;

public class UpdateQuestMessage {
    private final QuestInfoModel quest;
    private final int usedDoubleXpAmount;
    private final int remainingDoubleXpAmount;
    private final boolean awardAvailable;
    private final UpdateXpMessage userLevel;

    public UpdateQuestMessage(Builder builder) {
        this.quest = builder.quest;
        this.usedDoubleXpAmount = builder.usedDoubleXpAmount;
        this.remainingDoubleXpAmount = builder.remainingDoubleXpAmount;
        this.userLevel = builder.userLevel;
        this.awardAvailable = builder.awardAvailable;
    }

    public static class Builder {
        private QuestInfoModel quest;
        private int usedDoubleXpAmount;
        private int remainingDoubleXpAmount;
        private UpdateXpMessage userLevel;
        private boolean awardAvailable;

        public Builder setQuest(QuestInfoModel quest) {
            this.quest = quest;
            return this;
        }

        public Builder setUsedDoubleXpAmount(int usedDoubleXpAmount) {
            this.usedDoubleXpAmount = usedDoubleXpAmount;
            return this;
        }

        public Builder setRemainingDoubleXpAmount(int remainingDoubleXpAmount) {
            this.remainingDoubleXpAmount = remainingDoubleXpAmount;
            return this;
        }

        public Builder setUserLevel(UpdateXpMessage userLevel) {
            this.userLevel = userLevel;
            return this;
        }

        public Builder setAwardAvailable(boolean awardAvailable) {
            this.awardAvailable = awardAvailable;
            return this;
        }

        public UpdateQuestMessage build() {
            return new UpdateQuestMessage(this);
        }
    }

}
