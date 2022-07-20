package com.mynet.shared.analytics.model;

import com.mynet.shared.analytics.enums.EventType;
import com.mynet.shared.logs.QueueElement;

import java.util.Map;

public class BaseEvent extends QueueElement {
    String userId;
    String deviceId;
    String sessionId;
    int eventName;
    Map<String,Object> gameData;
    long eventDate;
    String platform;
    String appVersion;
    long eventDateMS;

    public BaseEvent(String userId, String deviceId, String sessionId, EventType eventName, Map<String, Object> gameData, String platform, String appVersion) {
        this.userId = userId;
        this.deviceId = deviceId;
        this.sessionId = sessionId;
        this.eventName = eventName.ordinal();
        this.gameData = gameData;
        long millis = System.currentTimeMillis();
        this.eventDate = millis / 1000;
        this.platform = platform;
        this.appVersion = appVersion;
        this.eventDateMS = millis;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public Map<String, Object> getGameData() {
        return gameData;
    }

    public void setGameData(Map<String, Object> gameData) {
        this.gameData = gameData;
    }
}
