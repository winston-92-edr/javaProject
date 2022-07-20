package com.mynet.shared.analytics.model;

public class SitTableSpecialEvent {
    long gameId;
    long start;
    long end;
    String from;

    public SitTableSpecialEvent(long gameId, String from) {
        this.gameId = gameId;
        this.start = System.currentTimeMillis();
        this.from = from;

    }

    public void setEnd() {
        this.end = System.currentTimeMillis();
    }

    public void setGameId(long gameId) {
        this.gameId = gameId;
    }

    public void setStart(long start) {
        this.start = start;
    }

    public void setEnd(long end) {
        this.end = end;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public long getGameId() {
        return gameId;
    }

    public long getStart() {
        return start;
    }

    public long getEnd() {
        return end;
    }

    public String getFrom() {
        return from;
    }
}
