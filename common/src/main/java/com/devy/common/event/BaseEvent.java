package com.devy.common.event;

import java.time.ZonedDateTime;

public class BaseEvent {
    public String eventId;
    public String eventType;
    public ZonedDateTime eventAt;

    public BaseEvent(String eventId, String eventType) {
        this.eventId = eventId;
        this.eventType = eventType;
        eventAt = ZonedDateTime.now();
    }

    public BaseEvent() {
    }

    public String getEventId() {
        return eventId;
    }

    public String getEventType() {
        return eventType;
    }

    public ZonedDateTime getEventAt() {
        return eventAt;
    }
}
