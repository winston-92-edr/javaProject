package com.mynet.chatserver.models;

public class SetChatUsersModel {
    private String groupId;
    private Object[] users;

    public SetChatUsersModel() {
    }

    public SetChatUsersModel(String groupId, Object[] users) {
        this.groupId = groupId;
        this.users = users;
    }

    public String getGroupId() {
        return groupId;
    }

    public Object[] getUsers() {
        return users;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public void setUsers(ChatUser[] users) {
        this.users = users;
    }
}
