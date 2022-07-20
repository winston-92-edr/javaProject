package com.mynet.gameserver.response;

import com.mynet.gameserver.enums.GameEndStatus;
import com.mynet.gameserver.model.HandOverCardModel;
import com.mynet.gameserver.model.TableUserShortModel;
import com.mynet.shared.model.UserTournamentModel;

import java.util.ArrayList;
import java.util.List;

public class HandOverResponse {

    private final String tableId;
    private final String winnerId;
    private final int whoFinishedSide;
    private final String partnerId;
    private final GameEndStatus gameEndStatus;
    private final List<TableUserShortModel> usersMoneyAndExperience;
    private final List<HandOverCardModel> finishedHand;
    private final long potValue;
    private final long finalHandOverMoney;
    private final boolean noMoreGameAtThisTable; //Sil
    private final String[] winnerIdList;
    private final UserTournamentModel userTournamentModel;
    private final boolean potBreak;
    private final long winningMoney;
    private final boolean promotion;

    public HandOverResponse(Builder builder) {
        this.tableId = builder.tableId;
        this.winnerId = builder.winnerId;
        this.whoFinishedSide = builder.whoFinishedSide;
        this.partnerId = builder.partnerId;
        this.gameEndStatus = builder.gameEndStatus;
        this.usersMoneyAndExperience = builder.usersMoneyAndExperience;
        this.finishedHand = builder.finishedHand;
        this.potValue = builder.potValue;
        this.finalHandOverMoney = builder.finalHandOverMoney;
        this.noMoreGameAtThisTable = builder.noMoreGameAtThisTable;
        this.winnerIdList = builder.winneIdList;
        this.userTournamentModel = builder.userTournamentModel;
        this.potBreak = builder.potBreak;
        this.winningMoney = builder.winningMoney;
        this.promotion = builder.promotion;
    }

    public String getTableId() {
        return tableId;
    }

    public long getPotValue() {
        return potValue;
    }

    public long getFinalHandOverMoney() {
        return finalHandOverMoney;
    }

    public boolean isNoMoreGameAtThisTable() {
        return noMoreGameAtThisTable;
    }

    public String[] getWinnerIdList() {
        return winnerIdList;
    }

    public static class Builder {
        private String tableId = "";
        private String winnerId = "";
        private String partnerId = "";
        private int whoFinishedSide;
        private GameEndStatus gameEndStatus;
        private List<TableUserShortModel> usersMoneyAndExperience = new ArrayList<>();
        private List<HandOverCardModel> finishedHand = new ArrayList<>();
        private long potValue;
        private long finalHandOverMoney;
        private boolean noMoreGameAtThisTable;
        private String[] winneIdList;
        private UserTournamentModel userTournamentModel;
        private boolean potBreak = false;
        private long winningMoney;
        private boolean promotion;

        public Builder setTableId(String tableId) {
            this.tableId = tableId;
            return this;
        }

        public Builder setWinneIdList(String[] fuidList) {
            if (fuidList != null && fuidList.length > 0) {
                this.winnerId = fuidList[0];
            }
            this.winneIdList = fuidList;
            return this;
        }

        public Builder setWinnerId(String winnerId) {
            this.winnerId = winnerId;
            return this;
        }

        public Builder setWhoFinishedSide(int whoFinishedSide) {
            this.whoFinishedSide = whoFinishedSide;
            return this;
        }

        public Builder setPartnerId(String partnerId) {
            this.partnerId = partnerId;
            return this;
        }

        public Builder setGameEndStatus(GameEndStatus gameEndStatus) {
            this.gameEndStatus = gameEndStatus;
            return this;
        }

        public Builder setUsersMoneyAndExperience(List<TableUserShortModel> usersMoneyAndExperience) {
            this.usersMoneyAndExperience = usersMoneyAndExperience;
            return this;
        }

        public Builder setFinishedHand(List<HandOverCardModel> finishedHand) {
            this.finishedHand = finishedHand;
            return this;
        }

        public Builder setPotValue(long potValue) {
            this.potValue = potValue;
            return this;
        }

        public Builder setFinalHandOverMoney(long finalHandOverMoney) {
            this.finalHandOverMoney = finalHandOverMoney;
            return this;
        }

        public Builder setNoMoreGameAtThisTable(boolean noMoreGameAtThisTable) {
            this.noMoreGameAtThisTable = noMoreGameAtThisTable;
            return this;
        }

        public Builder setUserTournamentModel(UserTournamentModel userTournamentModel) {
            this.userTournamentModel = userTournamentModel;
            return this;
        }

        public Builder setPotBreak(boolean potBreak) {
            this.potBreak = potBreak;
            return this;
        }

        public Builder setWinningMoney(long winningMoney) {
            this.winningMoney = winningMoney;
            return this;
        }

        public Builder setPromotion(boolean promotion) {
            this.promotion = promotion;
            return this;
        }

        public HandOverResponse build() {
            return new HandOverResponse(this);
        }
    }
}
