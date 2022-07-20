package com.mynet.bonusservice.model.rules;
import com.mynet.bonusservice.model.type.ComparisonTypes;
import com.mynet.shared.logs.BonusLog;

public class BonusRule {
    long amount;
    ComparisonTypes comparisonType;

    public boolean isValid(BonusLog request){
        return true;
    }
}
