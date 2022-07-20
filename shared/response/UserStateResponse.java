package com.mynet.shared.response;

import com.mynet.gameserver.model.TableUserModel;
import com.mynet.gameserver.response.TableInfoResponse;

import java.util.List;

public class UserStateResponse {
    TableInfoResponse tableInfo;
    boolean audience;
    int roomId;
    int remainingTimeToStart;
    private boolean partner;
    private List<TableUserModel> gamers;
    private List<TableUserModel> audiences;
    private String roomName;
    private int sideCount;
    private int tournamentId;
    private int totalTime;

    public UserStateResponse(TableInfoResponse tableInfo, boolean audience, int roomId, int remainingTimeToStart, boolean partner, List<TableUserModel> gamers, List<TableUserModel> audiences, String roomName, int tournamentId, int sideCount, int totalTime) {
        this.tableInfo = tableInfo;
        this.audience = audience;
        this.roomId = roomId;
        this.remainingTimeToStart = remainingTimeToStart;
        this.partner = partner;
        this.gamers = gamers;
        this.audiences = audiences;
        this.roomName = roomName;
        this.sideCount = sideCount;
        this.tournamentId = tournamentId;
        this.totalTime = totalTime;
    }
}

