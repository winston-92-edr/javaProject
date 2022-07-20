package com.mynet.questservice.quests.messages;

import com.mynet.questservice.quests.models.QuestInfoAwardModel;

import java.util.ArrayList;

public class UpdateAwardsMessage {
    ArrayList<QuestInfoAwardModel> awards;

    public UpdateAwardsMessage(ArrayList<QuestInfoAwardModel> awards) {
        this.awards = awards;
    }
}
