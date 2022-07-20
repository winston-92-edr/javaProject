package com.mynet.tableservice.actions;

import com.mynet.shared.model.BasicUserModel;
import com.mynet.tableservice.service.TableService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RemoveUserRoomAction extends AbstractServiceAction{
    private static Logger logger = LoggerFactory.getLogger(RemoveUserRoomAction.class);
    BasicUserModel user;

    public RemoveUserRoomAction(BasicUserModel user) {
        this.user = user;
    }

    @Override
    public void process() {
        try {
            TableService.getInstance().updateUserRoom(user, false);
        }catch (Exception ex){
            logger.error(ex.getMessage(), ex);
        }
    }
}
