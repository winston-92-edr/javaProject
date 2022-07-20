package com.mynet.shared.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.ConstructorProperties;

public class UserTournamentModel {
    private static final Logger logger = LoggerFactory.getLogger(UserTournamentModel.class);

    public static final String F_LEVEL = "level";
    public static final String F_LOSTGAMES = "lostgames";
    public static final String F_TITLE = "title";
    public static final String F_DESC = "desc";
    public static final String F_FAIL_POINT = "f_point";
    public static final String F_AWARD = "award";
    public static final String F_STATE = "state";
    public static final String F_LAST_LEVEL = "is_last_level";
    public static final String F_REMAINING_TRY_COUNT = "remaining_try_count";
    public static final String F_CLAIMED = "claimed";
    public static final String F_TID = "tid";
    public static final String F_TOURNAMENT_ID= "tournamentId";
    public static final String F_END_DATE = "endDate";

    public static final String F_PROXYID = "proxyID";
    public static final String F_GAMEID = "gameID";
    public static final String F_GENERALID = "generalID";
    public static final String F_ROOMID = "roomID";
    public static final String F_TABLEID = "tableID";

    public long tid; // user tournament id
    public int lostGames; // turnuvanin sonuna kadar tututlur.
    public int level;
    public String title;
    public String desc;
    public int failPoint;
    public int award;
    public UserTournamentState state;
    public boolean claimed;
    public boolean isLastLevel;
    public int remainingTryCount;
    public int tournamentId;
    public long endDate;
    public boolean expired;


    @ConstructorProperties({"tid", "lost", "level", "fail", "award", "state", "claimed", "is_last_level", "rem_try_count", "tourId", "endDate"})
    public UserTournamentModel(long tid, byte lostGames, byte level, byte failPoint, int award, byte state, byte claimed, byte isLastLevel, byte remainingTryCount, int tournamentId, long endDate) {
        this.tid = tid;
        this.lostGames = lostGames;
        this.level = level;
        this.failPoint = failPoint;
        this.award = award;
        this.state = UserTournamentState.values()[state];
        this.claimed = claimed == (byte) 1;
        this.isLastLevel = isLastLevel == (byte) 1;
        this.remainingTryCount = remainingTryCount;
        this.tournamentId = tournamentId;
        this.endDate = endDate;
        this.title = "";
        this.desc = "";
    }

    private UserTournamentModel(long tid, int lostGames, int level, String title, String desc, int failPoint, int award, UserTournamentState state, boolean claimed, boolean isLastLevel, int remainingTryCount, int tournamentId, long endDate) {
        this.tid = tid;
        this.lostGames = lostGames;
        this.level = level;
        this.title = title;
        this.desc = desc;
        this.failPoint = failPoint;
        this.award = award;
        this.state = state;
        this.claimed = claimed;
        this.isLastLevel = isLastLevel;
        this.remainingTryCount = remainingTryCount;
        this.tournamentId = tournamentId;
        this.endDate = endDate;
    }

    public boolean isFailed() {
        return lostGames >= failPoint;
    }

    public boolean isSucceed() { return isLastLevel; }

    public boolean isOver() { return isSucceed() || isFailed(); }

    public static class Builder {
        private int level;
        private int lostGames;
        private String title;
        private String desc;
        private int failPoint;
        private int award;
        private UserTournamentState state;
        private boolean isLastLevel;
        private int remainingTryCount;
        private boolean claimed;
        private long tid;
        private int tournamentId;
        private long endDate;
        private boolean expired;

        public Builder(int level) {
            this.level = level;
        }

        public Builder setLostGames(int val) {
            this.lostGames = val;
            return this;
        }

        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder setDesc(String desc) {
            this.desc = desc;
            return this;
        }

        public Builder setFailPoint(int failPoint) {
            this.failPoint = failPoint;
            return this;
        }

        public Builder setAward(int award) {
            this.award = award;
            return this;
        }

        public Builder setState(UserTournamentState state) {
            this.state = state;
            return this;
        }

        public Builder setClaimed(boolean claimed) {
            this.claimed = claimed;
            return this;
        }

        public Builder setLastLevel(boolean lastLevel) {
            isLastLevel = lastLevel;
            return this;
        }

        public Builder setRemainingTryCount(int remainingTryCount) {
            this.remainingTryCount = remainingTryCount;
            return this;
        }

        public Builder setTid(long tid){
            this.tid = tid;
            return this;
        }

        public Builder setEndDate(long endDate) {
            this.endDate = endDate;
            return this;
        }

        public UserTournamentModel build() {
            return new UserTournamentModel(tid, lostGames, level, title, desc, failPoint, award, state, claimed, isLastLevel, remainingTryCount, tournamentId, endDate);
        }

        public Builder setTournamentId(int tournamentId){
            this.tournamentId = tournamentId;
            return this;
        }

        public Builder setExpired(boolean expired){
            this.expired = expired;
            return this;
        }
    }

    public static UserTournamentModel create(int levelId, TournamentLevel levelInfo, int failPoint, int lastLevelId, int remainingTryCount, int tournamentId, long endDate) {
        return new UserTournamentModel.Builder(levelId)
                .setTitle(levelInfo.getTitle())
                .setDesc(levelInfo.getDescription())
                .setFailPoint(failPoint)
                .setAward(levelInfo.getAward())
                .setState(UserTournamentState.ACTIVE)
                .setClaimed(false)
                .setLastLevel(lastLevelId <= levelId)
                .setRemainingTryCount(remainingTryCount)
                .setTid(System.currentTimeMillis())
                .setTournamentId(tournamentId)
                .setEndDate(endDate)
                .build();
    }
}
