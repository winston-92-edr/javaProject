package com.mynet.shared.resource.processors;

import com.mynet.shared.model.ServerEventModel;
import com.mynet.shared.resource.db.DBEventProcessor;
import com.mynet.shared.user.UserController;

public class BanUserEventProcessor implements DBEventProcessor {
    @Override
    public void process(ServerEventModel event) {
        UserController.getInstance().removeUser(event.getEventData());
    }
}
