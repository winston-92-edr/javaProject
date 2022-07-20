package com.mynet.questservice.quests.models;

import com.mynet.questservice.QuestController;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class UserQuestLevelModel {
    private AtomicInteger xp;
    private AtomicInteger level;
    private AtomicInteger totalXp;

    //Integer: levelId, Integer: consumeState => 0/1
    private ConcurrentHashMap<Integer, Integer> awards;

    public UserQuestLevelModel(int xp, ConcurrentHashMap<Integer, Integer> awards, int level) {
        this.xp = new AtomicInteger(xp);
        this.awards = awards;
        this.level = new AtomicInteger(level);
        this.totalXp = new AtomicInteger(0);
        this.calculateTotalXp();
    }

    public int getXp() {
        return xp.get();
    }

    public void setXp(int xp) {
        this.xp.set(xp);
    }

    public int getLevel() {
        return level.get();
    }

    public int setLevel(int level) {
        this.level.lazySet(level);
        return this.level.intValue();
    }

    public int getTotalXp() {
        return totalXp.get();
    }

    public int setTotalXp(int totalXp) {
        this.totalXp.lazySet(totalXp);
        return this.totalXp.get();
    }

    public ConcurrentHashMap<Integer, Integer> getAwards() {
        return awards;
    }

    public void setAwards(ConcurrentHashMap<Integer, Integer> awards) {
        this.awards = awards;
    }

    public void addAward(int awardId) {
        awards.put(awardId, 0);
    }

    public void claimAward(int awardId) {
        awards.put(awardId, 1);
    }

    //Total xp earned in this season
    private void calculateTotalXp() {
        HashMap<Integer,Integer> levels = QuestController.getInstance().getLevelXps();

        for(Integer levelId: levels.keySet()){
            if(levelId < getLevel()) {
                setTotalXp(getTotalXp() + QuestController.getInstance().getLevelGoal(levelId));
            }
        }

        setTotalXp(getTotalXp() + getXp());
    }

    public boolean awardAvailable(){
       return awards.entrySet().stream().filter(x -> x.getValue().equals(0)).count() > 0;
    }

    @Override
    public String toString() {
        return "UserQuestLevelModel{" +
                "xp=" + xp +
                ", level=" + level +
                ", totalXp=" + totalXp +
                ", awards=" + awards +
                '}';
    }
}
