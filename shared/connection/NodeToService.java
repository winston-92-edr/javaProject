package com.mynet.shared.connection;

import com.mynet.shared.network.*;
import com.mynet.tableservice.processors.*;
import com.mynet.tableservice.service.TableService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NodeToService implements ServerToServerMessageProcessor {
    private static final Logger logger = LoggerFactory.getLogger(NodeToService.class);

    public NodeToService() {
        registerMessageProcessors();
    }

    private void registerMessageProcessors() {
        MessageProcessController.getInstance().registerCommand(GameCommands.ADD_USER, new AddUserProcessor());
        MessageProcessController.getInstance().registerCommand(GameCommands.REMOVE_USER, new RemoveUserProcessor());
        MessageProcessController.getInstance().registerCommand(GameCommands.TABLES_INFO, new SetTableInfoProcessor());
        MessageProcessController.getInstance().registerCommand(GameCommands.ADD_TABLE, new AddTableProcessor());
        MessageProcessController.getInstance().registerCommand(GameCommands.REMOVE_TABLE, new RemoveTableProcessor());
        MessageProcessController.getInstance().registerCommand(GameCommands.UPDATE_TABLE, new UpdateTableProcessor());
        MessageProcessController.getInstance().registerCommand(GameCommands.QUICK_PLAY, new QuickPlayProcessor());
        MessageProcessController.getInstance().registerCommand(GameCommands.QUICK_PLAY_2, new QuickPlayProcessor());
        MessageProcessController.getInstance().registerCommand(GameCommands.SIT_TABLE_2, new SitTableProcessor());
        MessageProcessController.getInstance().registerCommand(GameCommands.JOIN_AN_AUDIENCE, new JoinAudienceProcessor());
        MessageProcessController.getInstance().registerCommand(GameCommands.GET_ROOMS_USERS_COUNT, new GetRoomUserCountProcessor());
        MessageProcessController.getInstance().registerCommand(GameCommands.ADD_USER_TO_ROOM, new AddUserRoomProcessor());
        MessageProcessController.getInstance().registerCommand(GameCommands.REMOVE_USER_FROM_ROOM, new RemoveUserRoomProcessor());
        MessageProcessController.getInstance().registerCommand(GameCommands.SERVER_USER_STATUS_UPDATED, new UserStatusUpdateProcessor());
        MessageProcessController.getInstance().registerCommand(GameCommands.ENTER_TABLE, new EnterTableProcessor());
        MessageProcessController.getInstance().registerCommand(GameCommands.GET_ROOM_TABLES, new GetRoomTablesProcessor());
        MessageProcessController.getInstance().registerCommand(GameCommands.GET_QUICK_PLAY_ROOMS, new GetQuickPlayRoomsProcessor());
    }

    @Override
    public void processMessage(NetworkMessage message) throws InvalidServerMessage {
        if (message.getCmd() != GameCommands.REMOVE_USER && message.getCmd() != GameCommands.ADD_USER) {
            if (message.getId() != null) {
                TableService.getInstance().fixUser(message.getId());
            }
        }

        MessageProcessController.getInstance().processMessage(message);
    }
}
