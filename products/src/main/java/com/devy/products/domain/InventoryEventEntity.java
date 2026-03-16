package com.devy.products.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;

import java.time.ZonedDateTime;
import java.util.Objects;


@IdClass(InventoryEventEntity.InventoryEventEntityId.class)
@Entity
public class InventoryEventEntity {
    @Column(length = 64)
    @Id
    private String eventId;
    @Column(length = 100)
    @Id
    private String aggregateType;
    @Column(length = 64)
    @Id
    private String aggregateId;
    @Column(length = 100)
    @Id
    private String eventType;
    private String eventPayload;
    private int eventSequence;
    private int eventVersion;
    private String metadata;
    private ZonedDateTime createdAt;

    public InventoryEventEntity() {
    }

    public InventoryEventEntity(
            String eventId,
            String aggregateType,
            String aggregateId,
            String eventType,
            String eventPayload,
            int eventSequence,
            int eventVersion,
            String metadata
    ) {
        this(eventId, aggregateType, aggregateId, eventType, eventPayload, eventSequence, eventVersion, metadata, ZonedDateTime.now());
    }

    public InventoryEventEntity(
            String eventId,
            String aggregateType,
            String aggregateId,
            String eventType,
            String eventPayload,
            int eventSequence,
            int eventVersion,
            String metadata,
            ZonedDateTime createdAt

    ) {
        this.eventId = eventId;
        this.aggregateType = aggregateType;
        this.aggregateId = aggregateId;
        this.eventType = eventType;
        this.eventPayload = eventPayload;
        this.eventSequence = eventSequence;
        this.eventVersion = eventVersion;
        this.metadata = metadata;
        this.createdAt = createdAt;
    }

    public String getEventId() {
        return eventId;
    }

    public String getAggregateType() {
        return aggregateType;
    }

    public String getAggregateId() {
        return aggregateId;
    }

    public String getEventType() {
        return eventType;
    }

    public String getEventPayload() {
        return eventPayload;
    }

    public int getEventSequence() {
        return eventSequence;
    }

    public int getEventVersion() {
        return eventVersion;
    }

    public String getMetadata() {
        return metadata;
    }

    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }

    public static class InventoryEventEntityId {
        private String eventId;
        private String aggregateType;
        private String aggregateId;
        private String eventType;

        public InventoryEventEntityId() {
        }

        public InventoryEventEntityId(String eventId, String aggregateType, String aggregateId, String eventType) {
            this.eventId = eventId;
            this.aggregateType = aggregateType;
            this.aggregateId = aggregateId;
            this.eventType = eventType;
        }

        public String getEventId() {
            return eventId;
        }

        public String getAggregateType() {
            return aggregateType;
        }

        public String getAggregateId() {
            return aggregateId;
        }

        public String getEventType() {
            return eventType;
        }

        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) return false;
            InventoryEventEntityId that = (InventoryEventEntityId) o;
            return Objects.equals(eventId, that.eventId) && Objects.equals(aggregateType, that.aggregateType) && Objects.equals(aggregateId, that.aggregateId) && Objects.equals(eventType, that.eventType);
        }

        @Override
        public int hashCode() {
            return Objects.hash(eventId, aggregateType, aggregateId, eventType);
        }
    }
}
