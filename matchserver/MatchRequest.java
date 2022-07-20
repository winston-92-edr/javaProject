package com.mynet.matchserver;

import com.mynet.matchserver.model.GameTypeInfo;

import java.util.Objects;

public class MatchRequest implements Comparable<MatchRequest> {
    private final GameUser user;
    private final GameTypeInfo gameTypeInfo;
    private final long matchmakingStartTime;
    private final int tournamentId;

    public MatchRequest(GameUser user, GameTypeInfo gameTypeInfo, int tournamentId) {
        this.user = user;
        this.gameTypeInfo = gameTypeInfo;
        this.tournamentId = tournamentId;
        this.matchmakingStartTime = System.currentTimeMillis();
    }

    public GameUser getUser() {
        return user;
    }

    public GameTypeInfo getGameTypeInfo() {
        return gameTypeInfo;
    }

    public long getMatchmakingStartTime() {
        return matchmakingStartTime;
    }

    public int getTournamentId() {
        return tournamentId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MatchRequest request = (MatchRequest) o;
        return Objects.equals(user.getId(), request.user.getId()) &&
                Objects.equals(gameTypeInfo.getGameType(), request.gameTypeInfo.getGameType());
    }

    @Override
    public int hashCode() {
        return Objects.hash(user.getId(), gameTypeInfo.getGameType());
    }

    @Override
    public String toString() {
        return "MatchRequest{" +
                "user=" + user.getId() +
                ", gameTypeInfo=" + gameTypeInfo.getGameType() +
                ", matchmakingStartTime=" + matchmakingStartTime +
                '}';
    }

    @Override
    public int compareTo(MatchRequest o) {
        if(o.getMatchmakingStartTime() > this.getMatchmakingStartTime()){
            return -1;
        }else {
            return 1;
        }
    }
}
