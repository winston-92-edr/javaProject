package com.mynet.shared.request;

public class AddFriendRequest {
    String id;
    boolean result;
    String invitingName;

    public String getId() {
        return id;
    }

    public boolean isResult() {
        return result;
    }

    public String getInvitingName() {
        return invitingName;
    }
}
