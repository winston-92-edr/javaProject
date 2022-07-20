package com.mynet.gameserver.response;

public class GetReadyResponse {
    int remainingTime;
    boolean kill;

    public GetReadyResponse(int remainingTime) {
        this.kill = false;
        this.remainingTime = remainingTime;
    }

    public GetReadyResponse(boolean kill) {
        this.kill = kill;
    }
}
