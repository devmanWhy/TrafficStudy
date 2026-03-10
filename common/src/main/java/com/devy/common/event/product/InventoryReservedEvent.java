package com.devy.common.event.product;

public class InventoryReservedEvent extends ProductEvent {
    private String userId;
    private String productId;
    private int quantity;
    private long totalAmount;

    public InventoryReservedEvent(String eventId, String userId, String productId, int quantity, long totalAmount) {
        super(eventId, InventoryReservedEvent.class.getName());
        this.userId = userId;
        this.productId = productId;
        this.quantity = quantity;
        this.totalAmount = totalAmount;
    }

    public InventoryReservedEvent(String userId, String productId, int quantity, long totalAmount) {
        this.userId = userId;
        this.productId = productId;
        this.quantity = quantity;
        this.totalAmount = totalAmount;
    }

    public InventoryReservedEvent() {
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
