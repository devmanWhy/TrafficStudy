package com.devy.orderetl.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.ZonedDateTime;

@Document(collection = "order_history")
public class OrderHistory {
    @Id
    private String orderId;
    private long totalAmount;
    private boolean charged;
    @Indexed
    private Long eventAt;


    public OrderHistory() {
    }

    public OrderHistory(String orderId, long totalAmount, boolean charged) {
        this(orderId, totalAmount, charged, ZonedDateTime.now().toInstant().toEpochMilli());
    }

    public OrderHistory(String orderId, long totalAmount, boolean charged, Long eventAt) {
        this.orderId = orderId;
        this.totalAmount = totalAmount;
        this.charged = charged;
        this.eventAt = eventAt;
    }

    public String getOrderId() {
        return orderId;
    }

    public long getTotalAmount() {
        return totalAmount;
    }

    public boolean isCharged() {
        return charged;
    }

    public Long getEventAt() {
        return eventAt;
    }
}
