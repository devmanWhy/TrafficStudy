package com.devy.common.event.order.command;

public class CancelOrderCommand extends OrderCommand {
    private String orderId;
    private String userId;

    public CancelOrderCommand(String orderId, String userId) {
        super(orderId, CancelOrderCommand.class.getName());
        this.orderId = orderId;
        this.userId = userId;
    }

    public CancelOrderCommand() {
        super();
    }

    public String getOrderId() {
        return orderId;
    }

    public String getUserId() {
        return userId;
    }
}
