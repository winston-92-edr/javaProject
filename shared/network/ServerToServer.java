package com.mynet.shared.network;

import com.mynet.shared.config.ServerConfiguration;
import io.netty.channel.Channel;

import java.net.InetSocketAddress;

public class ServerToServer {
    public enum ServerType{
        PROXY,
        GAME,
        SOCIAL,
        MATCH,
        TABLE,
        QUEST,
        BONUS,
        CHAT
    }

    private final int id;
    private Channel channel;
    private final int port;
    private ServerType type;
    private NetworkMessageQueue worker;
    public ServerToServerMessageProcessor messageProcessor;


    public ServerToServer(ServerType type, int id, Channel channel, ServerToServerMessageProcessor messageProcessor){
        this.type = type;
        this.id = id;
        this.channel = channel;
        this.port = ((InetSocketAddress) channel.localAddress()).getPort();
        this.messageProcessor = messageProcessor;

        int workerCount = ServerConfiguration.getInt("worker.count", 1);
        this.worker = new NetworkMessageQueue(workerCount);
        this.worker.init();
    }

    public void addMessage(NetworkMessageWrapper message){
        worker.addMessage(message);
    }

    public int getPort() {
        return port;
    }

    public Channel getChannel() {
        return channel;
    }
    public void setChannel(Channel channel) {
        this.channel = channel;
    }
    public int getId() {
        return id;
    }

    public ServerType getType() {
        return type;
    }
}
