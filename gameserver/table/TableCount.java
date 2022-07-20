package com.mynet.gameserver.table;

public class TableCount {
    private final int playing;
    private final int waiting;

    public TableCount(int playing, int waiting) {
        this.playing = playing;
        this.waiting = waiting;
    }

    public int getPlaying() {
        return playing;
    }

    public int getWaiting() {
        return waiting;
    }
}
