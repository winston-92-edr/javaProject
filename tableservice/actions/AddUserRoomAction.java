package com.mynet.tableservice.actions;

import com.mynet.shared.model.BasicUserModel;
import com.mynet.tableservice.service.TableService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AddUserRoomAction extends AbstractServiceAction{
    private static Logger logger = LoggerFactory.getLogger(AddUserRoomAction.class);
    BasicUserModel user;

    public AddUserRoomAction(BasicUserModel user) {
        this.user = user;
    }

    @Override
    public void process() {
        try {
            TableService.getInstance().updateUserRoom(user, true);
        }catch (Exception ex){
            logger.error(ex.getMessage(), ex);
        }
    }
}
