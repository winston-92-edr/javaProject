package com.mynet.bonusservice.model.rules;
import com.mynet.bonusservice.model.type.ComparisonTypes;
import com.mynet.shared.logs.BonusLog;

public class MLAmountRule extends BonusRule{
    @Override
    public boolean isValid(BonusLog log) {
        if(comparisonType == ComparisonTypes.EQUAL) return log.getMoney() == amount;
        else if(comparisonType == ComparisonTypes.GREATER) return log.getMoney() > amount;
        else return log.getMoney() < amount;
    }
}
