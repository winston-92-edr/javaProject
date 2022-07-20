package com.mynet.socialserver.request;

public class AddUserSocialRequest {
    private int friendsCount;

    public int getFriendsCount(){ return friendsCount;}

    public AddUserSocialRequest(int friendsCount) {
        this.friendsCount = friendsCount;
    }
}
