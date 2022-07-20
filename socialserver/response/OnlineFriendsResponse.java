package com.mynet.socialserver.response;

import com.mynet.socialserver.model.FriendModel;

import java.util.List;

public class OnlineFriendsResponse {
    private final List<FriendModel> friends;

    public OnlineFriendsResponse(List<FriendModel> friends) {
        this.friends = friends;
    }
}
