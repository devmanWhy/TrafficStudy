package com.devy.products.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;

import java.time.ZonedDateTime;
import java.util.Objects;


@IdClass(EventSnapshotEntity.EventSnapshotEntityId.class)
@Entity
public class EventSnapshotEntity {
    @Column(length = 64)
    @Id
    private String aggregateId;
    @Column(length = 100)
    @Id
    private String aggregateType;
    @Column(length = 2000)
    private String entityPayload;
    private int eventSequence;

    private ZonedDateTime createdAt;

    public EventSnapshotEntity() {
    }

    public EventSnapshotEntity(String aggregateId, String aggregateType, String entityPayload, int eventSequence, ZonedDateTime createdAt) {
        this.aggregateId = aggregateId;
        this.aggregateType = aggregateType;
        this.entityPayload = entityPayload;
        this.eventSequence = eventSequence;
        this.createdAt = createdAt;
    }

    public String getAggregateType() {
        return aggregateType;
    }

    public String getAggregateId() {
        return aggregateId;
    }

    public int getEventSequence() {
        return eventSequence;
    }

    public String getEntityPayload() {
        return entityPayload;
    }

    public ZonedDateTime getCreatedAt() {
        return createdAt;
    }

    public static class EventSnapshotEntityId {
        private String aggregateId;
        private String aggregateType;

        public EventSnapshotEntityId() {
        }

        public EventSnapshotEntityId(String aggregateId, String aggregateType) {
            this.aggregateId = aggregateId;
            this.aggregateType = aggregateType;
        }

        public String getAggregateType() {
            return aggregateType;
        }

        public String getAggregateId() {
            return aggregateId;
        }

        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) return false;
            EventSnapshotEntityId that = (EventSnapshotEntityId) o;
            return Objects.equals(aggregateId, that.aggregateId) && Objects.equals(aggregateType, that.aggregateType);
        }

        @Override
        public int hashCode() {
            return Objects.hash(aggregateId, aggregateType);
        }
    }
}
