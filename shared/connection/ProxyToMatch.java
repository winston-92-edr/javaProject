package com.mynet.shared.connection;

import com.mynet.matchserver.MatchMakingController;
import com.mynet.matchserver.processors.AddUserProcessor;
import com.mynet.matchserver.processors.RemoveUserProcessor;
import com.mynet.matchserver.processors.TournamentCancelMatchingProcessor;
import com.mynet.shared.network.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.mynet.matchserver.processors.TournamentMatchingProcessor;

public class ProxyToMatch implements ServerToServerMessageProcessor {
    private static final Logger logger = LoggerFactory.getLogger(ProxyToMatch.class);

    public ProxyToMatch() {
        registerMessageProcessors();
    }

    private void registerMessageProcessors() {
        MessageProcessController.getInstance().registerCommand(GameCommands.ADD_USER, new AddUserProcessor());
        MessageProcessController.getInstance().registerCommand(GameCommands.REMOVE_USER, new RemoveUserProcessor());
        MessageProcessController.getInstance().registerCommand(GameCommands.TOURNAMENT_MATCHMAKING, new TournamentMatchingProcessor());
        MessageProcessController.getInstance().registerCommand(GameCommands.CANCEL_MATCHMAKING, new TournamentCancelMatchingProcessor());
    }

    @Override
    public void processMessage(NetworkMessage request) throws InvalidServerMessage {
        if (request.getCmd() != GameCommands.REMOVE_USER && request.getCmd() != GameCommands.ADD_USER) {
            if (request.getId() != null) {
                MatchMakingController.getInstance().fixUser(request.getId());
            }
        }

        MessageProcessController.getInstance().processMessage(request);
    }
}
