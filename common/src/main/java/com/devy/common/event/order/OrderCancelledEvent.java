package com.devy.common.event.order;

public class OrderCancelledEvent extends OrderEvent {
    private String orderId;
    public OrderCancelledEvent(String orderId) {
        super(orderId, OrderCancelledEvent.class.getName());
        this.orderId = orderId;
    }

    public OrderCancelledEvent() {}

    public String getOrderId() {
        return orderId;
    }

}
