package com.mynet.shared.analytics.model;

import com.mynet.shared.analytics.enums.EventType;

import java.util.Map;

public class GameEconomy extends BaseEvent{
    long balance;
    float transaction;
    String transactionType;
    String source;

    public GameEconomy(String userId, String devId, String sessionId, EventType eventType, Map<String, Object> gameData, long balance, float transaction, String transactionType, String source, String platform, String appVersion) {
        super(userId, devId, sessionId, eventType, gameData, platform, appVersion);
        this.balance = balance;
        this.transaction = transaction;
        this.transactionType = transactionType;
        this.source = source;
    }



    public long getBalance() {
        return balance;
    }

    public void setBalance(long balance) {
        this.balance = balance;
    }

    public float getTransaction() {
        return transaction;
    }

    public void setTransaction(float transaction) {
        this.transaction = transaction;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }
}
