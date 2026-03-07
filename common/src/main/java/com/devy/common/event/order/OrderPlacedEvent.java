package com.devy.common.event.order;

public class OrderPlacedEvent extends OrderEvent {

    String userId;
    String productId;
    int quantity;
    long totalAmount;

    public OrderPlacedEvent(String eventId, String userId, String productId, int quantity, long totalAmount) {
        super(eventId, OrderPlacedEvent.class.getName());
        this.userId = userId;
        this.productId = productId;
        this.quantity = quantity;
        this.totalAmount = totalAmount;
    }

    public OrderPlacedEvent() {
        super();
    }

    public String getUserId() {
        return userId;
    }

    public String getProductId() {
        return productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public long getTotalAmount() {
        return totalAmount;
    }

    @Override
    public String toString() {
        return "OrderPlacedEvent{" +
                "totalAmount=" + totalAmount +
                ", userId='" + userId + '\'' +
                ", productId='" + productId + '\'' +
                ", quantity=" + quantity +
                ", eventId='" + eventId + '\'' +
                ", eventType='" + eventType + '\'' +
                '}';
    }
}
