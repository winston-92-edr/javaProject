package com.mynet.shared.analytics.model;

import com.mynet.shared.analytics.enums.EventType;

import java.util.Map;

public class SpecialEvent extends BaseEvent{
    String specialEventName;
    String stage1;
    String stage2;
    String stage3;

    public SpecialEvent(String userId, String deviceId, String sessionId, EventType eventType, Map<String, Object> gameData, String specialEventName, String stage1, String stage2, String stage3, String platform, String appVersion) {
        super(userId, deviceId, sessionId, eventType, gameData, platform, appVersion);
        this.specialEventName = specialEventName;
        this.stage1 = stage1;
        this.stage2 = stage2;
        this.stage3 = stage3;
    }

    public String getSpecialEventName() {
        return specialEventName;
    }

    public void setSpecialEventName(String specialEventName) {
        this.specialEventName = specialEventName;
    }

    public String getStage1() {
        return stage1;
    }

    public void setStage1(String stage1) {
        this.stage1 = stage1;
    }

    public String getStage2() {
        return stage2;
    }

    public void setStage2(String stage2) {
        this.stage2 = stage2;
    }

    public String getStage3() {
        return stage3;
    }

    public void setStage3(String stage3) {
        this.stage3 = stage3;
    }
}
