package com.mynet.shared.response;

import com.mynet.shared.types.ClaimAwardType;

public class ClaimAwardResponse {
    private final ClaimAwardType awardType;
    private final long amount;
    private final long finalMoney;
    private final boolean isConsumed;
    private final String desc;
    private final int awardId;
    private final String tournamentBadge;

    private ClaimAwardResponse(ClaimAwardType awardType, long amount, long finalMoney, boolean isConsumed, String desc, int awardId,String badge ) {
        this.awardType = awardType;
        this.amount = amount;
        this.finalMoney = finalMoney;
        this.isConsumed = isConsumed;
        this.desc = desc;
        this.awardId = awardId;
        this.tournamentBadge = badge;
    }

    public static class Builder {
        private ClaimAwardType type;
        private long amount;
        private long finalMoney;
        private boolean isConsumed;
        private String desc;
        private int awardId;
        private String tournamentBadge;


        public Builder(ClaimAwardType type) {
            this.type = type;
        }


        public Builder setAmount(long amount) {
            this.amount = amount;
            return this;
        }

        public Builder setFinalMoney(long finalMoney) {
            this.finalMoney = finalMoney;
            return this;
        }

        public Builder setConsumed(boolean consumed) {
            isConsumed = consumed;
            return this;
        }

        public Builder setDesc(String desc) {
            this.desc = desc;
            return this;
        }

        public Builder setAwardId(int awardId) {
            this.awardId = awardId;
            return this;
        }

        public Builder setTournamentBadge(String badge){
            this.tournamentBadge = badge;
            return this;
        }

        public ClaimAwardResponse buid() {
            return new ClaimAwardResponse(type, amount, finalMoney, isConsumed, desc, awardId,tournamentBadge);
        }
    }
}
