package com.devy.common.event.product.command;

public class CreateInventoryCommand extends ProductCommand {
    private String productId;
    private int quantity;

    public CreateInventoryCommand(String eventId, String productId, int quantity) {
        super(eventId, CreateInventoryCommand.class.getName());
        this.productId = productId;
        this.quantity = quantity;
    }

    public CreateInventoryCommand() {
        super();
    }

    public String getProductId() {
        return productId;
    }

    public int getQuantity() {
        return quantity;
    }

}
