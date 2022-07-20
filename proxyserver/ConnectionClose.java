package com.mynet.proxyserver;

public class ConnectionClose {
    private String reason;

    public ConnectionClose(String reason) {
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
