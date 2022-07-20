package com.mynet.questservice.quests.category;

public class LostGameCategoryInfo extends QuestCategoryInfo{
    private long bet;
    private boolean classic;
    private boolean paired;

    public LostGameCategoryInfo(String fuid, long bet, boolean classic, boolean paired) {
        this.fuid = fuid;
        this.bet = bet;
        this.classic = classic;
        this.paired = paired;
    }

    public long getBet() {
        return bet;
    }

    public boolean isClassic() {
        return classic;
    }

    public boolean isPaired() {
        return paired;
    }
}
