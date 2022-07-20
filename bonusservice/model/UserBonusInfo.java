package com.mynet.bonusservice.model;

import java.beans.ConstructorProperties;

public class UserBonusInfo {
    private int bonusId;
    private int count;

    public UserBonusInfo(int bonusId) {
        this.bonusId = bonusId;
        this.count = 1;
    }

    @ConstructorProperties({"bonus_id","count"})
    public UserBonusInfo(int bonusId, int count) {
        this.bonusId = bonusId;
        this.count = count;
    }

    public int getBonusId() {
        return bonusId;
    }

    public int getCount() {
        return count;
    }

    public void increaseCount() {
        count++;
    }
}
