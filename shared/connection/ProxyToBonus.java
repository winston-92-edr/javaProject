package com.mynet.shared.connection;

import com.mynet.bonusservice.BonusService;
import com.mynet.bonusservice.processors.AddUserProcessor;
import com.mynet.bonusservice.processors.RemoveUserProcessor;
import com.mynet.shared.network.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProxyToBonus implements ServerToServerMessageProcessor{
    private static final Logger logger = LoggerFactory.getLogger(com.mynet.shared.connection.ProxyToBonus.class);
    private MessageProcessController messageController;

    public ProxyToBonus() {
        messageController = MessageProcessController.getInstance();

        registerMessageProcessors();
    }

    public void registerMessageProcessors() {
        messageController.registerCommand(GameCommands.ADD_USER, new AddUserProcessor());
        messageController.registerCommand(GameCommands.REMOVE_USER, new RemoveUserProcessor());
    }

    public void processMessage(NetworkMessage request) throws InvalidServerMessage {
        if (request.getCmd() != GameCommands.REMOVE_USER && request.getCmd() != GameCommands.ADD_USER) {
            if (request.getId() != null) {
                BonusService.getInstance().resetUser(request.getId());
            }
        }

        messageController.processMessage(request);
    }
}


