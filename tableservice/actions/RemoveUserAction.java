package com.mynet.tableservice.actions;

import com.mynet.tableservice.service.TableService;

public class RemoveUserAction extends AbstractServiceAction{
    private String userId;

    public RemoveUserAction(String userId) {
        this.userId = userId;
    }

    @Override
    public void process() {
        TableService.getInstance().removeUser(userId);
    }
}
