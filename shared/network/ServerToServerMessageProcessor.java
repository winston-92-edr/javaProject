package com.mynet.shared.network;

public interface ServerToServerMessageProcessor {
    public void processMessage(NetworkMessage message) throws InvalidServerMessage;
}