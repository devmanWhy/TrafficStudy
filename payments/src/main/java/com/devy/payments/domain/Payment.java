package com.devy.payments.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.time.ZonedDateTime;

@Entity
public class Payment {
    @Id
    private String paymentId;
    private String orderId;
    private String userId;
    private long amount;
    private ZonedDateTime paidAt;

    public Payment() {
    }

    public Payment(String paymentId, String orderId, String userId, long amount) {
        this(paymentId, orderId, userId, amount, ZonedDateTime.now());
    }

    public Payment(String paymentId, String orderId, String userId, long amount, ZonedDateTime paidAt) {
        this.paymentId = paymentId;
        this.orderId = orderId;
        this.userId = userId;
        this.amount = amount;
        this.paidAt = paidAt;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getUserId() {
        return userId;
    }

    public long getAmount() {
        return amount;
    }

    public ZonedDateTime getPaidAt() {
        return paidAt;
    }
}
