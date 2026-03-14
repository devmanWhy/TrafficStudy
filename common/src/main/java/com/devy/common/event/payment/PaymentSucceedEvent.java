package com.devy.common.event.payment;

public class PaymentSucceedEvent extends PaymentEvent {
    private String orderId;
    private String userId;

    public PaymentSucceedEvent(String orderId, String userId) {
        super(orderId, PaymentSucceedEvent.class.getName());
        this.orderId = orderId;
        this.userId = userId;
    }

    public PaymentSucceedEvent() {
        super();
    }

    public String getOrderId() {
        return orderId;
    }

    public String getUserId() {
        return userId;
    }
}
