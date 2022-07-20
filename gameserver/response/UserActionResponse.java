package com.mynet.gameserver.response;

public class UserActionResponse {
    private final long tableId;
    private final int side;
    private final int nextTurn;
    private final int visibleSide;
    private final String cardId;
    private final int time;
    private final boolean autoplay;

    public UserActionResponse(Builder builder) {
        this.tableId = builder.tableId;
        this.side = builder.side;
        this.nextTurn = builder.nextTurn;
        this.cardId = builder.cardId;
        this.visibleSide = builder.visibleSide;
        this.time =  builder.time;
        this.autoplay = builder.autoplay;
    }

    public static class Builder{
        private long tableId;
        private int side;
        private int nextTurn;
        private int visibleSide;
        private String cardId;
        private int time;
        private boolean autoplay;

        public Builder setTableId(long tableId) {
            this.tableId = tableId;
            return this;
        }

        public Builder setSide(int side) {
            this.side = side;
            return this;
        }

        public Builder setNextTurn(int nextTurn) {
            this.nextTurn = nextTurn;
            return this;
        }

        public Builder setVisibleSide(int visibleSide) {
            this.visibleSide = visibleSide;
            return this;
        }

        public Builder setCardId(String cardId) {
            this.cardId = cardId;
            return this;
        }

        public Builder setTime(int time) {
            this.time = time;
            return this;
        }

        public Builder setAutoplay(boolean autoplay) {
            this.autoplay = autoplay;
            return this;
        }

        public UserActionResponse build(){
            return new UserActionResponse(this);
        }

    }
}
