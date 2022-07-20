package com.mynet.tableservice.service;

public class ServiceProxyDataWrapper {
    private String data;
    private int proxyId;

    public ServiceProxyDataWrapper(String data, int proxyId) {
        this.data = data;
        this.proxyId = proxyId;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public int getProxyId() {
        return proxyId;
    }

    public void setProxyId(int proxyId) {
        this.proxyId = proxyId;
    }
}
