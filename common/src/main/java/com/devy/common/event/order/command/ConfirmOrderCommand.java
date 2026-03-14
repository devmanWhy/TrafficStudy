package com.devy.common.event.order.command;

public class ConfirmOrderCommand extends OrderCommand {
    private String orderId;
    private String userId;

    public ConfirmOrderCommand(String orderId, String userId) {
        super(orderId, ConfirmOrderCommand.class.getName());
        this.orderId = orderId;
        this.userId = userId;
    }

    public ConfirmOrderCommand() {
        super();
    }

    public String getOrderId() {
        return orderId;
    }

    public String getUserId() {
        return userId;
    }

    @Override
    public String toString() {
        return "CancelOrderCommand{" +
                "orderId='" + orderId + '\'' +
                ", userId='" + userId + '\'' +
                '}';
    }
}
