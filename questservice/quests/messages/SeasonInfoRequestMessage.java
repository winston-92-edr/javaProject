package com.mynet.questservice.quests.messages;

public class SeasonInfoRequestMessage {
    private final String updateKey;

    public SeasonInfoRequestMessage(String updateKey) {
        this.updateKey = updateKey;
    }

    public String getUpdateKey() {
        return updateKey;
    }
}
