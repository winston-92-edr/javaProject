package com.mynet.proxyserver.observer;

import com.mynet.observer.ObserverRunnable;
import com.mynet.shared.network.MessageProcessController;
import com.mynet.shared.network.NetworkMessage;
import com.mynet.shared.user.ProxyUser;

public class ProxyRequest extends ObserverRunnable {
    ProxyUser user;
    NetworkMessage request;
    String id;

    public ProxyRequest() {
        this.id = "ProxyRequest_" + System.currentTimeMillis();
    }

    @Override
    protected void setData(Object data) {
        RequestData message = (RequestData) data;
        this.user = message.user;
        this.request = message.request;
    }


    @Override
    protected String getId() {
        return this.id;
    }

    @Override
    public void run() {
        try {
            request.setId(user.getId());
            MessageProcessController.getInstance().processMessage(request);
        } catch (Exception e) {

        }
    }
}
