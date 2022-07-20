package com.mynet.shared.logs;

import com.google.gson.Gson;

public class QueueElement {
    public byte[] getBytes() {
        Gson gson = new Gson();
        String message = gson.toJson(this);
        return message.getBytes();
    }
}
