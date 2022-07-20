package com.mynet.bonusservice.model.rules;

import com.mynet.bonusservice.model.type.ComparisonTypes;
import com.mynet.shared.logs.BonusLog;

public class GameCountRule extends BonusRule{

    @Override
    public boolean isValid(BonusLog log) {
        if(comparisonType == ComparisonTypes.EQUAL) return log.getGameCount() == amount;
        else if(comparisonType == ComparisonTypes.GREATER) return log.getGameCount() > amount;
        else return log.getGameCount() < amount;
    }
}
