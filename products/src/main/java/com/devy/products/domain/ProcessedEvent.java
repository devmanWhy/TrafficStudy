package com.devy.products.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;

import java.time.ZonedDateTime;
import java.util.Objects;

@IdClass(ProcessedEvent.ProcessedEventId.class)
@Entity
public class ProcessedEvent {
    @Id
    String eventId;
    @Id
    String eventType;
    @Column(length = 1000)
    String eventPayload;
    ZonedDateTime processedAt;

    public ProcessedEvent() {
    }

    public ProcessedEvent(String eventId, String eventType, String eventPayload) {
        this(eventId, eventType, eventPayload, ZonedDateTime.now());
    }

    public ProcessedEvent(String eventId, String eventType, String eventPayload, ZonedDateTime processedAt) {
        this.eventId = eventId;
        this.eventType = eventType;
        this.eventPayload = eventPayload;
        this.processedAt = processedAt;
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

    public ZonedDateTime getProcessedAt() {
        return processedAt;
    }

    public static class ProcessedEventId {
        String eventId;
        String eventType;

        public ProcessedEventId(String eventId, String eventType) {
            this.eventId = eventId;
            this.eventType = eventType;
        }

        public ProcessedEventId() {
        }

        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) return false;
            ProcessedEventId that = (ProcessedEventId) o;
            return Objects.equals(eventId, that.eventId) && Objects.equals(eventType, that.eventType);
        }

        @Override
        public int hashCode() {
            return Objects.hash(eventId, eventType);
        }
    }
}
