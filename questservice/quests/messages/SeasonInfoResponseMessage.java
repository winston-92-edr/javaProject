package com.mynet.questservice.quests.messages;

import com.mynet.questservice.quests.models.QuestInfoAwardModel;
import com.mynet.questservice.quests.models.QuestInfoModel;
import com.mynet.questservice.quests.models.UserPreviousSeasonModel;
import com.mynet.questservice.quests.models.UserQuestLevelInfoModel;

import java.util.ArrayList;
import java.util.HashMap;

public class SeasonInfoResponseMessage {
    private final int season;
    private final UserQuestLevelInfoModel userLevel;
    private final long seasonTtl;
    private final boolean availableAward;
    private final long nextQuestsTtl;
    private final boolean hasPass;
    private final HashMap<Integer,Integer> levelXps;
    private final ArrayList<QuestInfoModel> quests;
    private final ArrayList<QuestInfoAwardModel> currentSeasonAwards;
    private final UserPreviousSeasonModel userPreviousSeason;
    private final boolean seasonHandshake;
    private final boolean open;
    private final boolean seasonCompleted;

    private SeasonInfoResponseMessage(Builder builder){
        this.season = builder.season;
        this.userLevel = builder.userLevel;
        this.seasonTtl = builder.seasonTtl;
        this.availableAward = builder.availableAward;
        this.nextQuestsTtl = builder.nextQuestsTtl;
        this.hasPass = builder.hasPass;
        this.levelXps = builder.levelXps;
        this.quests = builder.quests;
        this.currentSeasonAwards = builder.currentSeasonAwards;
        this.userPreviousSeason = builder.userPreviousSeason;
        this.seasonHandshake = builder.seasonHandshake;
        this.open = builder.open;
        this.seasonCompleted =  builder.seasonCompleted;
    }

    @Override
    public String toString() {
        return "SeasonInfoResponseMessage{" +
                "season=" + season +
                ", userLevel=" + userLevel +
                ", seasonTtl=" + seasonTtl +
                ", availableAward=" + availableAward +
                ", nextQuestsTtl=" + nextQuestsTtl +
                ", hasPass=" + hasPass +
                ", levelXps=" + levelXps +
                ", quests=" + quests +
                ", currentSeasonAwards=" + currentSeasonAwards +
                ", userPreviousSeason=" + userPreviousSeason +
                ", seasonHandshake=" + seasonHandshake +
                ", open=" + open +
                ", seasonCompleted=" + seasonCompleted +
                '}';
    }

    public static class Builder{
        private int season;
        private UserQuestLevelInfoModel userLevel;
        private long seasonTtl;
        private boolean availableAward;
        private long nextQuestsTtl;
        private boolean hasPass;
        private HashMap<Integer,Integer> levelXps;
        private ArrayList<QuestInfoModel> quests;
        private ArrayList<QuestInfoAwardModel> currentSeasonAwards;
        private UserPreviousSeasonModel userPreviousSeason;
        private boolean seasonHandshake;
        private boolean open;
        private boolean seasonCompleted;


        public Builder season(int season) {
            this.season = season;
            return this;
        }

        public Builder userLevel(UserQuestLevelInfoModel userLevel){
            this.userLevel = userLevel;
            return this;
        }

        public Builder seasonTtl(long seasonTtl) {
            this.seasonTtl = seasonTtl;
            return this;
        }

        public Builder availableAward(boolean availableAward) {
            this.availableAward = availableAward;
            return this;
        }

        public Builder nextQuestsTtl(long nextQuestsTtl) {
            this.nextQuestsTtl = nextQuestsTtl;
            return this;
        }

        public Builder hasPass(boolean hasPass) {
            this.hasPass = hasPass;
            return this;
        }

        public Builder levelXps(HashMap<Integer, Integer> levelXps) {
            this.levelXps = levelXps;
            return this;
        }

        public Builder quests(ArrayList<QuestInfoModel> quests) {
            this.quests = quests;
            return this;
        }

        public Builder currentSeasonAwards(ArrayList<QuestInfoAwardModel> currentSeasonAwards) {
            this.currentSeasonAwards = currentSeasonAwards;
            return this;
        }

        public Builder userPreviousSeason(UserPreviousSeasonModel userPreviousSeason) {
            this.userPreviousSeason = userPreviousSeason;
            return this;
        }

        public Builder seasonHandShake(boolean seasonHandshake) {
            this.seasonHandshake = seasonHandshake;
            return this;
        }

        public Builder open(boolean open) {
            this.open = open;
            return this;
        }

        public Builder seasonCompleted(boolean seasonCompleted){
            this.seasonCompleted = seasonCompleted;
            return this;
        }

        public SeasonInfoResponseMessage build(){
            return new SeasonInfoResponseMessage(this);
        }
    }
}
