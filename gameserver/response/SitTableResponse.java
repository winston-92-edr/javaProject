package com.mynet.gameserver.response;

import com.mynet.gameserver.enums.GameStatus;

public class SitTableResponse {
    private final long tableId;
    private final int side;
    private final String userId;
    private final boolean isAudience;
    private final GameStatus gameStatus;

    public SitTableResponse(Builder builder) {
        this.tableId = builder.tableId;
        this.side = builder.side;
        this.userId = builder.userId;
        this.isAudience = builder.isAudience;
        this.gameStatus = builder.gameStatus;
    }

    @Override
    public String toString() {
        return "SitTableResponse{" +
                "tableId=" + tableId +
                ", side=" + side +
                ", userId='" + userId + '\'' +
                ", isAudience=" + isAudience +
                ", gameStatus=" + gameStatus +
                '}';
    }

    public static class Builder{
        private long tableId;
        private int side;
        private String userId;
        private boolean isAudience;
        private GameStatus gameStatus;

        public Builder setTableId(long tableId) {
            this.tableId = tableId;
            return this;
        }

        public Builder setSide(int side) {
            this.side = side;
            return this;
        }

        public Builder setUserId(String userId) {
            this.userId = userId;
            return this;
        }

        public Builder setIsAudience(boolean isAudience) {
            this.isAudience = isAudience;
            return this;
        }

        public Builder setGameStatus(GameStatus gameStatus) {
            this.gameStatus = gameStatus;
            return this;
        }

        public SitTableResponse build(){
            return new SitTableResponse(this);
        }
    }
}
