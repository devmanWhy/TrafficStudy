package com.devy.common.event;

public class BaseCommand {
    public String eventId;
    public String eventType;

    public BaseCommand(String eventId, String eventType) {
        this.eventId = eventId;
        this.eventType = eventType;
    }
    public BaseCommand() {
    }

    public String getEventId() {
        return eventId;
    }

    public String getEventType() {
        return eventType;
    }
}
