package com.mynet.shared.connection;

import com.mynet.questservice.QuestController;
import com.mynet.questservice.processors.*;
import com.mynet.shared.network.*;

public class ProxyToQuest implements ServerToServerMessageProcessor {

    public ProxyToQuest() {
        registerMessageProcessors();
    }

    private void registerMessageProcessors(){
        MessageProcessController.getInstance().registerCommand(GameCommands.ADD_USER, new AddUserProcessor());
        MessageProcessController.getInstance().registerCommand(GameCommands.REMOVE_USER, new RemoveUserProcessor());
        MessageProcessController.getInstance().registerCommand(GameCommands.SEASON_INFO, new SeasonInfoProcessor());
        MessageProcessController.getInstance().registerCommand(GameCommands.CHECK_QUEST,new CheckQuestProcessor());
    }

    @Override
    public void processMessage(NetworkMessage request) throws InvalidServerMessage {
        if (request.getCmd() != GameCommands.REMOVE_USER && request.getCmd() != GameCommands.ADD_USER) {
            if (request.getId() != null) {
                QuestController.getInstance().fixUser(request.getId());
            }
        }

        MessageProcessController.getInstance().processMessage(request);
    }
}
