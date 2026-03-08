package com.devy.orders.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Outbox {
    @Id
    private String eventId;
    private String eventType;
    private String eventPayload;
    private boolean success;

    public Outbox() {
    }

    public Outbox(String eventId, String eventType, String eventPayload) {
        this(eventId, eventType, eventPayload, false);
    }

    public Outbox(String eventId, String eventType, String eventPayload, boolean success) {
        this.eventId = eventId;
        this.eventType = eventType;
        this.eventPayload = eventPayload;
        this.success = success;
    }

    public String getEventId() {
        return eventId;
    }

    public String getEventType() {
        return eventType;
    }

    public String getEventPayload() {
        return eventPayload;
    }

    public boolean isSuccess() {
        return success;
    }

    public void complete() {
        this.success = true;
    }

    @Override
    public String toString() {
        return "Outbox{" +
                "eventId='" + eventId + '\'' +
                ", eventType='" + eventType + '\'' +
                ", eventPayload='" + eventPayload + '\'' +
                ", success=" + success +
                '}';
    }
}
