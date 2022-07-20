package com.mynet.shared.network;

import io.netty.channel.Channel;
import com.mynet.shared.types.MessageWrapperType;

public class NetworkMessageWrapper {
    private NetworkMessage message;
    private Channel channel;
    private MessageWrapperType type;

    public NetworkMessageWrapper(NetworkMessage message, Channel channel) {
        this.message = message;
        this.channel = channel;
        this.type = MessageWrapperType.NETWORK;
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public NetworkMessage getMessage() {
        return message;
    }

    public void setMessage(NetworkMessage message) {
        this.message = message;
    }

    public MessageWrapperType getType() {
        return type;
    }
}