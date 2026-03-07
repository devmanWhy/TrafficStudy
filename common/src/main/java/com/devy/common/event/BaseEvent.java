package com.devy.common.event;

public class BaseEvent {
    public String eventId;
    public String eventType;

    public BaseEvent(String eventId, String eventType) {
        this.eventId = eventId;
        this.eventType = eventType;
    }
    public BaseEvent() {
    }

    public String getEventId() {
        return eventId;
    }

    public String getEventType() {
        return eventType;
    }
}
