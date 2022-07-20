package com.mynet.shared.network;

public interface MessageProcessor {
    public void process(NetworkMessage message) throws InvalidServerMessage;
}
