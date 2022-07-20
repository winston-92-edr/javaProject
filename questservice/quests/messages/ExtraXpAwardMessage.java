package com.mynet.questservice.quests.messages;

public class ExtraXpAwardMessage {
    int amount;
    int totalAmount;

    public ExtraXpAwardMessage(int amount, int totalAmount) {
        this.amount = amount;
        this.totalAmount = totalAmount;
    }
}
