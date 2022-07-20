package com.mynet.shared.connection;

import com.mynet.chatserver.models.ChatUser;
import com.mynet.gameserver.enums.ErrorCode;
import com.mynet.matchserver.GameUser;
import com.mynet.questservice.quests.models.QuestUser;
import com.mynet.shared.network.*;
import com.mynet.shared.response.ErrorResponse;
import com.mynet.shared.user.ProxyUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.mynet.shared.response.ServerMessageResponse;

public class NodeToProxy {
    private static final Logger logger = LoggerFactory.getLogger(NodeToProxy.class);
    public void addServerMessage(NetworkMessage message, int proxyID){
        ServerToServer connection = ServerToServerController.getInstance().getConnection(proxyID);
        if(connection != null) {
            connection.addMessage(new NetworkMessageWrapper(message.copy(), connection.getChannel()));
        }
    }

    // User class => for proxy and social server
    public void addServerMessage(NetworkMessage response, ProxyUser user) {
        ServerToServer connection = ServerToServerController.getInstance().getConnection(user.getProxyID());
        if(connection != null) {
            response.setId(user.getId());
            connection.addMessage(new NetworkMessageWrapper(response.copy(), connection.getChannel()));
        }
    }

    // User class => for proxy and quest server
    public void addServerMessage(NetworkMessage response, QuestUser user) {
        ServerToServer connection = ServerToServerController.getInstance().getConnection(user.getProxyID());
        if(connection != null) {
            response.setId(user.getId());
            connection.addMessage(new NetworkMessageWrapper(response.copy(), connection.getChannel()));
        }
    }

    // User class => for match and game server
    public void addServerMessage(NetworkMessage response, GameUser user) {
        ServerToServer connection = ServerToServerController.getInstance().getConnection(user.getProxyId());
        if(connection != null) {
            response.setId(user.getId());
            connection.addMessage(new NetworkMessageWrapper(response.copy(), connection.getChannel()));
        }
    }

    // User class => for chat and game server
    public void addServerMessage(NetworkMessage response, ChatUser user) {
        ServerToServer connection = ServerToServerController.getInstance().getConnection(user.getProxyId());
        if(connection != null) {
            response.setId(user.getId());
            connection.addMessage(new NetworkMessageWrapper(response.copy(), connection.getChannel()));
        }
    }

    public void addServerMessage(NetworkMessage response, String userId, int proxyId) {
        ServerToServer connection = ServerToServerController.getInstance().getConnection(proxyId);
        if(connection != null) {
            response.setId(userId);
            connection.addMessage(new NetworkMessageWrapper(response.copy(), connection.getChannel()));
        }
    }


    public void sendServerMessage(ProxyUser user, String msg) {
        NetworkMessage response = new NetworkMessage(GameCommands.SERVER_MESSAGE);
        response.setId(user.getId());
        response.setDataAsJSON(new ServerMessageResponse(msg));
        addServerMessage(response, user);
    }

    public void sendCustomError(ProxyUser user, ErrorCode code, String data) {
        ErrorResponse response = new ErrorResponse(code, data);
        NetworkMessage error = new NetworkMessage(GameCommands.ERROR);
        error.setDataAsJSON(response);
        addServerMessage(error, user);
    }

    public void sendError(ProxyUser user, ErrorCode code) {
        NetworkMessage error = new NetworkMessage(GameCommands.ERROR);
        error.setDataAsJSON(new ErrorResponse(code));
        addServerMessage(error, user);
    }

    public void sendError(GameUser user, ErrorCode code) {
        NetworkMessage error = new NetworkMessage(GameCommands.ERROR);
        error.setDataAsJSON(new ErrorResponse(code));
        addServerMessage(error, user);
    }
}
