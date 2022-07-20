package com.mynet.shared.response;

public class GoToYourFriendResponse {
    int gameId;
    int roomId;

    public GoToYourFriendResponse(int gameId, int roomId) {
        this.gameId = gameId;
        this.roomId = roomId;
    }
}
