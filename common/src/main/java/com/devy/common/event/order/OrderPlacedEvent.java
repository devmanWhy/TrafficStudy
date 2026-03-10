package com.devy.common.event.order;

public class OrderPlacedEvent extends OrderEvent {

    private String userId;
    private String productId;
    private int quantity;
    private long totalAmount;

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

    public OrderPlacedEvent(String userId, String productId, int quantity, long totalAmount) {
        super();
        this.userId = userId;
        this.productId = productId;
        this.quantity = quantity;
        this.totalAmount = totalAmount;
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
