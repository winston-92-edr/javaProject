package com.mynet.shared.logs;

public class ChatThreadLog extends QueueElement {
    private String lobby;
    private String line;

    public ChatThreadLog(String lobby, String line) {
        this.lobby = lobby;
        this.line = line;
    }

    public String getLobby() {
        return lobby;
    }

    public void setLobby(String lobby) {
        this.lobby = lobby;
    }

    public String getLine() {
        return line;
    }

    public void setLine(String line) {
        this.line = line;
    }
}
