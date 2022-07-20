package com.mynet.socialserver.model;

import java.beans.ConstructorProperties;

public class FriendRequestModel {
    String invitingFuid;
    String invitedFuid;
    int status;

    @ConstructorProperties({"inviting_fuid","invited_fuid","status"})
    public FriendRequestModel(long invitingFuid, long invitedFuid, int status) {
        this.invitingFuid = String.valueOf(invitingFuid);
        this.invitedFuid = String.valueOf(invitedFuid);
        this.status = status;
    }

    public FriendRequestModel(String invitingFuid, String invitedFuid, int status) {
        this.invitingFuid = invitingFuid;
        this.invitedFuid = invitedFuid;
        this.status = status;
    }

    public String getInvitingFuid() {
        return invitingFuid;
    }

    public void setInvitingFuid(String invitingFuid) {
        this.invitingFuid = invitingFuid;
    }

    public String getInvitedFuid() {
        return invitedFuid;
    }

    public void setInvitedFuid(String invitedFuid) {
        this.invitedFuid = invitedFuid;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
