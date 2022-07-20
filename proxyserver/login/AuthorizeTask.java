package com.mynet.proxyserver.login;

import com.mynet.proxyserver.user.UserModel;
import com.mynet.shared.resource.CacheController;
import io.netty.channel.Channel;
import com.mynet.proxyserver.model.LoginRequest;
import com.mynet.shared.resource.db.DBController;

import java.util.HashSet;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicLong;

public class AuthorizeTask implements Callable<UserModel> {
    private final LoginRequest request;
    private final Channel channel;
    private final UserConnectionHandler handler;
    private final String fuid;
    private final String platform;
    private boolean guest;
    private HashSet<String> blackList;
    private AtomicLong blackListUpdateTime;

    public AuthorizeTask(LoginRequest request, Channel channel, UserConnectionHandler handler, String fuid, String platform) {
        this.request = request;
        this.channel = channel;
        this.handler = handler;
        this.fuid = fuid;
        this.platform = platform;
        this.blackList = new HashSet<>();
        this.blackListUpdateTime = new AtomicLong(0);
    }

    public LoginRequest getRequest() {
        return request;
    }

    public Channel getChannel() {
        return channel;
    }

    public UserConnectionHandler getHandler() {
        return handler;
    }

    public String getFuid() {
        return fuid;
    }

    public String getPlatform() {
        return platform;
    }

    public boolean isGuest() {
        return guest;
    }

    public void setGuest(boolean guest) {
        this.guest = guest;
    }

    @Override
    public UserModel call() throws Exception {

        long millis = System.currentTimeMillis();
        if(blackListUpdateTime.get() + 60000 < millis){
            blackList = CacheController.getInstance().getBlackList();
            blackListUpdateTime.set(millis);
        }

        if(blackList.contains(fuid)){
            return null;
        }

        UserModel user = DBController.getInstance().getUser(fuid);
        if(user == null) return null;
        this.setGuest(user.guest);

        return user;

    }
}
