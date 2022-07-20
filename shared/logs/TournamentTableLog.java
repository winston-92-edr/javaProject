package com.mynet.shared.logs;

public class TournamentTableLog extends QueueElement {
    private final int tableCount;
    private final int removingTableCount;
    private final int activeTables;
    private final int inactiveTables;
    private final int tableSize;

    private TournamentTableLog(int tableCount, int removingTableCount, int activeTables, int inactiveTables, int tableSize) {
        this.tableCount = tableCount;
        this.removingTableCount = removingTableCount;
        this.activeTables = activeTables;
        this.inactiveTables = inactiveTables;
        this.tableSize = tableSize;
    }

    public int getTableCount() {
        return tableCount;
    }

    public int getRemovingTableCount() {
        return removingTableCount;
    }

    public int getActiveTables() {
        return activeTables;
    }

    public int getInactiveTables() {
        return inactiveTables;
    }

    public static class Builder{
        private int tableCount;
        private int removingTableCount;
        private int activeTables;
        private int inactiveTables;
        private int tableSize;

        public Builder tableCount(final int count){
            this.tableCount = count;
            return this;
        }

        public Builder removingTableCount(final int count){
            this.removingTableCount = count;
            return this;
        }

        public Builder activeTables(final int count){
            this.activeTables = count;
            return this;
        }

        public Builder inactiveTables(final int count){
            this.inactiveTables = count;
            return this;
        }
        public Builder tableSize(final int count){
            this.tableSize = count;
            return this;
        }

        public TournamentTableLog build(){
            return new TournamentTableLog(this.tableCount, this.removingTableCount, this.activeTables, this.inactiveTables, this.tableSize);
        }
    }


}
