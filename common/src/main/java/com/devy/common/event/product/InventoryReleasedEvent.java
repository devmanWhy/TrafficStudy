package com.devy.common.event.product;

public class InventoryReleasedEvent extends ProductEvent {
    private String userId;
    private String productId;
    private int quantity;
    private long totalAmount;

    public InventoryReleasedEvent(String eventId, String userId, String productId, int quantity, long totalAmount) {
        super(eventId, InventoryReleasedEvent.class.getName());
        this.userId = userId;
        this.productId = productId;
        this.quantity = quantity;
        this.totalAmount = totalAmount;
    }

    public InventoryReleasedEvent() {
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
}
