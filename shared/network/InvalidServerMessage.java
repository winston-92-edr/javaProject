package com.mynet.shared.network;

public class InvalidServerMessage extends Exception {
    private String serverMessage;

    public InvalidServerMessage(String serverMessage) {
        this.serverMessage = serverMessage;
    }
}