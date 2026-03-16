package com.devy.common.event.product;

public class InventoryCreatedEvent extends ProductEvent {
    private String productId;
    private int quantity;

    public InventoryCreatedEvent(String eventId, String productId, int quantity) {
        super(eventId, InventoryCreatedEvent.class.getName());
        this.productId = productId;
        this.quantity = quantity;
    }

    public InventoryCreatedEvent() {
        super();
    }

    public String getProductId() {
        return productId;
    }

    public int getQuantity() {
        return quantity;
    }


}
