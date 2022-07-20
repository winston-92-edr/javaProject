package com.mynet.bonusservice.model.rules;
import com.mynet.bonusservice.model.type.ComparisonTypes;
import com.mynet.shared.logs.BonusLog;

public class TicketAmountRule extends BonusRule{
    @Override
    public boolean isValid(BonusLog log) {
        if(comparisonType == ComparisonTypes.EQUAL) return log.getTicket() == amount;
        else if(comparisonType == ComparisonTypes.GREATER) return log.getTicket() > amount;
        else return log.getTicket() < amount;
    }
}
