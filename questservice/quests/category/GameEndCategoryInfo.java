package com.mynet.questservice.quests.category;

public class GameEndCategoryInfo extends QuestCategoryInfo {
    private boolean classic;
    private boolean paired;
    private boolean potBroken;
    private boolean withDouble;
    private boolean withOkey;
    private int longestTile;
    private long bet;
    private boolean withFakeOkey;

    public GameEndCategoryInfo(String fuid, boolean classic, boolean paired, boolean potBroken, boolean withDouble, boolean withOkey, int longestTile, long bet, boolean withFakeOkey) {
        this.fuid = fuid;
        this.classic = classic;
        this.paired = paired;
        this.potBroken = potBroken;
        this.withDouble = withDouble;
        this.withOkey = withOkey;
        this.longestTile = longestTile;
        this.bet = bet;
        this.withFakeOkey = withFakeOkey;
    }

    public boolean isClassic() {
        return classic;
    }

    public boolean isPaired() {
        return paired;
    }

    public boolean isPotBroken() {
        return potBroken;
    }

    public boolean isWithDouble() {
        return withDouble;
    }

    public boolean isWithOkey() {
        return withOkey;
    }

    public int getLongestTile() {
        return longestTile;
    }

    public long getBet() {
        return bet;
    }

    public boolean isWithFakeOkey() {
        return withFakeOkey;
    }
}
