package com.mynet.shared.response;

import com.mynet.socialserver.model.FriendModel;

import java.util.ArrayList;

public class GetActiveFriendsResponse {
    ArrayList<FriendModel> friends;

    public GetActiveFriendsResponse(ArrayList<FriendModel> friends) {
        this.friends = friends;
    }
}
