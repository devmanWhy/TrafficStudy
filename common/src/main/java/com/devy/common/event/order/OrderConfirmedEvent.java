package com.devy.common.event.order;

public class OrderConfirmedEvent extends OrderEvent{
    private String orderId;
    public OrderConfirmedEvent(String orderId) {
        super(orderId, OrderConfirmedEvent.class.getName());
        this.orderId = orderId;
    }
    public OrderConfirmedEvent() {}
    public String getOrderId() {
        return orderId;
    }
}
