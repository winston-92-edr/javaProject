package com.mynet.proxyserver.observer;

import com.mynet.shared.network.NetworkMessage;
import com.mynet.shared.user.ProxyUser;

public class RequestData {
    ProxyUser user;
    NetworkMessage request;

    public RequestData(ProxyUser user, NetworkMessage request) {
        this.user = user;
        this.request = request;
    }
}
