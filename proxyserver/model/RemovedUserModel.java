package com.mynet.proxyserver.model;

public class RemovedUserModel {
    private String id;
    private int proxyId;

    public RemovedUserModel() {
    }

    public RemovedUserModel(String id, int proxyId) {
        this.id = id;
        this.proxyId = proxyId;
    }

    public String getId() {
        return id;
    }

    public int getProxyId() {
        return proxyId;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setProxyId(int proxyId) {
        this.proxyId = proxyId;
    }
}
