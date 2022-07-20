package com.mynet.shared.model;

import com.mynet.shared.types.ServerEventType;

import java.beans.ConstructorProperties;

public class ServerEventModel {
    private int id;
    private int sid;
    private ServerEventType eventType;
    private String eventData;
    private long date;

    @ConstructorProperties({"id", "sid", "event_type", "event_data", "date"})
    public ServerEventModel(int id, int sid, String eventType, String eventData, long date) {
        this.id = id;
        this.sid = sid;
        this.eventType = ServerEventType.forCode(eventType);
        this.eventData = eventData;
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public int getSid() {
        return sid;
    }

    public ServerEventType getEventType() {
        return eventType;
    }

    public String getEventData() {
        return eventData;
    }

    public long getDate() {
        return date;
    }
}
