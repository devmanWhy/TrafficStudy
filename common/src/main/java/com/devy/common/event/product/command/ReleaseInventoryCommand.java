package com.devy.common.event.product.command;

public class ReleaseInventoryCommand extends ProductCommand {
    private String orderId;
    private String productId;
    private int quantity;

    public ReleaseInventoryCommand(String orderId, String productId, int quantity) {
        super(orderId, ReleaseInventoryCommand.class.getName());
        this.orderId = orderId;
        this.productId = productId;
        this.quantity = quantity;
    }

    public ReleaseInventoryCommand() {
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
