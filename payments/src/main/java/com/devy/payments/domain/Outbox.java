package com.devy.payments.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;

import java.util.Objects;

@IdClass(Outbox.OutboxId.class)
@Entity
public class Outbox {
    @Id
    private String eventId;
    @Id
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

    public static class OutboxId {
        String eventId;
        String eventType;

        public OutboxId(String eventId, String eventType) {
            this.eventId = eventId;
            this.eventType = eventType;
        }

        public OutboxId() {
        }

        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) return false;
            OutboxId outboxId = (OutboxId) o;
            return Objects.equals(eventId, outboxId.eventId) && Objects.equals(eventType, outboxId.eventType);
        }

        @Override
        public int hashCode() {
            return Objects.hash(eventId, eventType);
        }
    }
}

