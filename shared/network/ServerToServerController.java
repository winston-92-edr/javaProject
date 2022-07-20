package com.mynet.shared.network;

import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;

public class ServerToServerController {
    private static final Logger logger = LoggerFactory.getLogger(ServerToServerController.class);

    private final ConcurrentHashMap<Integer,ServerToServer> connections;
    private static ServerToServerController INSTANCE;

    public static ServerToServerController getInstance(){
        if(INSTANCE == null){
            INSTANCE = new ServerToServerController();
        }
        return INSTANCE;
    }

    private ServerToServerController(){
        connections = new ConcurrentHashMap<>();
    }

    public ConcurrentHashMap<Integer,ServerToServer> getConnections(){
        return connections;
    }

    public ServerToServer registerConnection(Channel channel, NetworkMessage msg, ServerToServerMessageProcessor messageProcessor, ServerToServer.ServerType type) {

        if(msg.getCmd() == GameCommands.REGISTER_NODE){

            try {
                RegisterProxyMessage message = NetworkMessage.CreateMessage(msg.getData(), RegisterProxyMessage.class);
                int proxyID = message.proxyID;

                if(proxyID > 0){
                    ServerToServer connection = connections.get(proxyID);
                    if(connection == null){
                        connection = new ServerToServer(type, proxyID, channel, messageProcessor);
                        connections.put(proxyID, connection);
                    }else{
                        connection.setChannel(channel);
                    }
                    return connection;
                }
            } catch (InvalidServerMessage invalidServerMessage) {
                logger.error("wrong message for registering proxy");
            }

        }

        return null;
    }

    public ServerToServer getConnection(int proxyID) {
        return connections.get(proxyID);
    }
}
