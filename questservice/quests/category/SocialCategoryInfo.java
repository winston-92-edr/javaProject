package com.mynet.questservice.quests.category;

public class SocialCategoryInfo extends QuestCategoryInfo{
    private int socialActionType;

    public SocialCategoryInfo(String fuid, int socialActionType) {
        this.fuid = fuid;
        this.socialActionType = socialActionType;
    }

    public int getSocialActionType() {
        return socialActionType;
    }
}
