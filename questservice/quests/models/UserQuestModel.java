package com.mynet.questservice.quests.models;

import java.beans.ConstructorProperties;
import java.util.concurrent.atomic.AtomicInteger;

public class UserQuestModel implements Comparable<UserQuestModel> {
    private final int questId;
    private AtomicInteger point;
    private int goal;

    @ConstructorProperties({"questId", "point"})
    public UserQuestModel(int questId, int point) {
        this.questId = questId;
        this.point = new AtomicInteger(point);
    }

    public int getQuestId() {
        return questId;
    }

    public int getPoint() {
        return point.intValue();
    }

    public void progress(int val){
        point.set(val);
    }

    public boolean isComplete(){
        return point.intValue() == goal;
    }

    public int getGoal() {
        return goal;
    }

    public void setGoal(int goal) {
        this.goal = goal;
    }

    public int add(int amount,QuestModel model) {
        if(getPoint() + amount <= getGoal()){
            setPoint(amount + getPoint());
        }
        else{
            setPoint(model.getGoal());
        }

        return getPoint();
    }

    public float getPercent(){
        return (float) point.get() / (float) goal;
    }

    public void setPoint(int point) {
        this.point.lazySet(point);
    }

    public int increment(){
        return this.point.incrementAndGet();
    }

    @Override
    public int compareTo(UserQuestModel next) {
        if(isComplete()){
            return 1;
        }else if(next.isComplete()){
            return -1;
        } else {
            return getPercent() > next.getPercent() ? -1 : 1;
        }
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 31 * hash + questId;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (this.getClass() != obj.getClass()) return false;
        UserQuestModel model = (UserQuestModel) obj;
        return questId == model.questId;
    }
}
