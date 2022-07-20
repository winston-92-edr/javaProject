package com.mynet.questservice.quests.messages;

public class AddUserMessage {
    String platform;

    public AddUserMessage(String platform) {
        this.platform = platform;
    }

    public String getPlatform() {
        return platform;
    }
}
