package com.mynet.questservice.quests.category;

public class MoneyCategoryInfo extends QuestCategoryInfo{
    private boolean classic;
    private boolean paired;
    private int amount;

    public MoneyCategoryInfo(String fuid, boolean classic, boolean paired, int amount) {
        this.fuid = fuid;
        this.classic = classic;
        this.paired = paired;
        this.amount = amount;
    }

    public boolean isClassic() {
        return classic;
    }
    public boolean isPaired() {
        return paired;
    }
    public int getAmount() {
        return amount;
    }
}
