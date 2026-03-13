package com.devy.common.event.product;

public class InventoryReservedEvent extends ProductEvent {
    private String orderId;
    private String productId;
    private int quantity;

    public InventoryReservedEvent(String orderId, String productId, int quantity) {
        super(orderId, InventoryReservedEvent.class.getName());
        this.orderId = orderId;
        this.productId = productId;
        this.quantity = quantity;
    }

    public InventoryReservedEvent() {
        super();
    }

    public String getOrderId() {
        return orderId;
    }

    public String getProductId() {
        return productId;
    }

    public int getQuantity() {
        return quantity;
    }
}

