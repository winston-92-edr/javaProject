package com.mynet.proxyserver.login;

import io.netty.channel.Channel;
import com.mynet.proxyserver.model.LoginRequest;

public class LoginTask {
    private LoginRequest request;
    private Channel channel;
    private UserConnectionHandler handler;

    public LoginTask(LoginRequest request, Channel channel, UserConnectionHandler handler) {
        this.request = request;
        this.channel = channel;
        this.handler = handler;
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public UserConnectionHandler getHandler() {
        return handler;
    }

    public void setHandler(UserConnectionHandler handler) {
        this.handler = handler;
    }

    public LoginRequest getRequest() {
        return request;
    }

    public void setRequest(LoginRequest request) {
        this.request = request;
    }
}
