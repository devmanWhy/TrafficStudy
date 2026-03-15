package com.devy.orderhistory.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.ZonedDateTime;

@Document(collection = "order_history")
public class OrderHistory {
    @Id
    private String historyId;

    @Indexed
    private String orderId;
    private String eventType;
    private String eventPayload;
    private Long eventAt;


    public OrderHistory() {
    }

    public OrderHistory(String historyId, String orderId, String eventType, String eventPayload) {
        this(
                historyId,
                orderId,
                eventType,
                eventPayload,
                ZonedDateTime.now().toInstant().toEpochMilli()
        );
    }

    public OrderHistory(String historyId, String orderId, String eventType, String eventPayload, long eventAt) {
        this.historyId = historyId;
        this.orderId = orderId;
        this.eventType = eventType;
        this.eventPayload = eventPayload;
        this.eventAt = eventAt;
    }

    public String getHistoryId() {
        return historyId;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getEventType() {
        return eventType;
    }

    public String getEventPayload() {
        return eventPayload;
    }


    public long getEventAt() {
        return eventAt;
    }
}
