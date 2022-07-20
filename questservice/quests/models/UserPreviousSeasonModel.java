package com.mynet.questservice.quests.models;

import java.util.ArrayList;
import java.util.HashMap;

public class UserPreviousSeasonModel {
    UserQuestLevelInfoModel previousUserLevel;
    ArrayList<QuestInfoAwardModel> previousSeasonAwards;
    boolean previousPass;
    private HashMap<Integer, Integer> levelXps;

    public UserPreviousSeasonModel(UserQuestLevelInfoModel previousUserLevel, ArrayList<QuestInfoAwardModel> previousSeasonAwards, boolean previousPass, HashMap<Integer, Integer> levelXps) {
        this.previousUserLevel = previousUserLevel;
        this.previousSeasonAwards = previousSeasonAwards;
        this.previousPass = previousPass;
        this.levelXps = levelXps;
    }

    @Override
    public String toString() {
        return "UserPreviousSeasonModel{" +
                "previousUserLevel=" + previousUserLevel +
                ", previousSeasonAwards=" + previousSeasonAwards +
                ", previousPass=" + previousPass +
                ", levelXps=" + levelXps +
                '}';
    }
}
