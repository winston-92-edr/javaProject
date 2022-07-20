package com.mynet.tableservice.actions;

import com.mynet.shared.model.BasicUserModel;
import com.mynet.tableservice.service.TableService;

public class AddUserAction extends AbstractServiceAction{
    BasicUserModel user;

    public AddUserAction(BasicUserModel user) {
        this.user = user;
    }

    @Override
    public void process() {
        TableService.getInstance().addUser(user);
    }
}
