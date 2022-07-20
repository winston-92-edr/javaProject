package com.mynet.bonusservice.model.rules;
import com.mynet.bonusservice.model.type.ComparisonTypes;
import com.mynet.shared.logs.BonusLog;

public class JoinDateRule extends BonusRule{
    @Override
    public boolean isValid(BonusLog log) {
        long condition = log.getJoinDate() + (amount * 60 * 60 * 1000);
        if(comparisonType == ComparisonTypes.EQUAL) return condition == System.currentTimeMillis();
        else if(comparisonType == ComparisonTypes.GREATER) return condition < System.currentTimeMillis();
        else return condition > System.currentTimeMillis();
    }
}
