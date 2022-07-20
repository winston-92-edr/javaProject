package com.mynet.shared.connection;

import com.mynet.chatserver.ChatController;
import com.mynet.chatserver.processors.*;
import com.mynet.shared.network.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProxyToChat implements ServerToServerMessageProcessor{
    private static final Logger logger = LoggerFactory.getLogger(ProxyToChat.class);
    private MessageProcessController messageController;

    public ProxyToChat() {
        messageController = MessageProcessController.getInstance();

        registerMessageProcessors();
    }

    public void registerMessageProcessors() {
        MessageProcessController.getInstance().registerCommand(GameCommands.ADD_USER, new AddUserProcessor());
        MessageProcessController.getInstance().registerCommand(GameCommands.REMOVE_USER, new RemoveUserProcessor());
        MessageProcessController.getInstance().registerCommand(GameCommands.CHAT_ENTER_TABLE, new EnterTableProcessor());
        MessageProcessController.getInstance().registerCommand(GameCommands.CHAT_LEFT_TABLE, new LeftTableProcessor());
        MessageProcessController.getInstance().registerCommand(GameCommands.TABLE_CHAT, new TableChatProcessor());
        MessageProcessController.getInstance().registerCommand(GameCommands.SET_PROFANITY_FILTER, new SetProfanityFilterProcessor());
    }

    public void processMessage(NetworkMessage request) throws InvalidServerMessage {
        if (request.getCmd() != GameCommands.REMOVE_USER && request.getCmd() != GameCommands.ADD_USER) {
            if (request.getId() != null) {
                ChatController.getInstance().fixUser(request.getId());
            }
        }

        messageController.processMessage(request);
    }
}


