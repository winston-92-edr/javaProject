package com.mynet.bonusservice.model;

import com.mynet.bonusservice.model.db.DbRule;
import com.mynet.bonusservice.model.db.DbRules;
import com.mynet.bonusservice.model.rules.*;
import com.mynet.bonusservice.model.type.BonusRuleTypes;
import com.mynet.shared.network.NetworkMessage;

import java.beans.ConstructorProperties;
import java.util.*;

public class BonusModel {
    private int id;
    private String name;
    private long ml;
    private int ticket;
    private List<BonusRule> rules;
    private int count;
    private int repeatInterval; //Hour
    private long endDate;
    private String message;
    private int triggerTime;
    private Set<BonusRuleTypes> ruleTypes;

    @ConstructorProperties({"id", "name", "ml", "ticket", "rules", "count", "repeat_interval", "end_date", "redis_notification_key", "message", "trigger_time"})
    public BonusModel(int id, String name, long ml, int ticket, String rules, int count, int repeatInterval, Date endDate, String redisNotificationKey, String message, int triggerTime) {
        this.id = id;
        this.name = name;

        this.ml = ml;
        this.ticket = ticket;
        this.count = count;

        this.repeatInterval = repeatInterval;
        this.endDate = endDate == null ? -1 : endDate.getTime();
        this.message = message;

        this.triggerTime = triggerTime;

        this.rules = new ArrayList<BonusRule>();
        this.ruleTypes = new HashSet<BonusRuleTypes>();

        DbRules dbRules = NetworkMessage.getGson().fromJson(rules, DbRules.class);


        for(DbRule rule: dbRules.getRules()){
            BonusRuleTypes type = BonusRuleTypes.forCode(rule.getType());

            BonusRule bonusRule = NetworkMessage.getGson().fromJson(rule.getValue(), BonusRule.class);

            switch (type){
                case JOIN_DATE:
                    this.rules.add(NetworkMessage.getGson().fromJson(rule.getValue(), JoinDateRule.class));
                    break;
                case ML_AMOUNT:
                    this.rules.add(NetworkMessage.getGson().fromJson(rule.getValue(), MLAmountRule.class));
                    break;
                case TICKET_AMOUNT:
                    this.rules.add(NetworkMessage.getGson().fromJson(rule.getValue(), TicketAmountRule.class));
                    break;
                case GAME_COUNT:
                    this.rules.add(NetworkMessage.getGson().fromJson(rule.getValue(), GameCountRule.class));
                    break;
            };

            this.ruleTypes.add(type);
        }
    }

    public String getName() {
        return name;
    }

    public long getMl() {
        return ml;
    }

    public int getTicket() {
        return ticket;
    }

    public List<BonusRule> getRules() {
        return rules;
    }

    public int getCount() {
        return count;
    }

    public Set<BonusRuleTypes> getRuleTypes() {
        return ruleTypes;
    }

    public String getMessage() {
        return message;
    }

    public int getId() {
        return id;
    }

    public int getRepeatInterval() {
        return repeatInterval;
    }

    public long getEndDate() {
        return endDate;
    }

    public int getTriggerTime() {
        return triggerTime;
    }
}
