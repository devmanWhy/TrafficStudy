package com.devy.common.event.product.command;

public class ReserveInventoryCommand extends ProductCommand {
    private String orderId;
    private String productId;
    private int quantity;

    public ReserveInventoryCommand(String orderId, String productId, int quantity) {
        super(orderId, ReserveInventoryCommand.class.getName());
        this.orderId = orderId;
        this.productId = productId;
        this.quantity = quantity;
    }

    public ReserveInventoryCommand() {
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
