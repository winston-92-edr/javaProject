package com.mynet.shared.request;

import java.util.ArrayList;

public class SetActiveFriendRequest {
    private ArrayList<String> friendList = new ArrayList<String>();

    public ArrayList<String> getFriendList() {
        return friendList;
    }
}
