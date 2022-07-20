package com.mynet.shared.response;

public class ServerMessageResponse {
    private String message;

    public ServerMessageResponse(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
