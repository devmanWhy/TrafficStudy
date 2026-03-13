package com.devy.common.event.payment.command;

public class DoPaymentCommand extends PaymentCommand {
    private String orderId;
    private String userId;
    private long totalAmount;

    public DoPaymentCommand(String orderId, String userId, long totalAmount) {
        super(orderId, DoPaymentCommand.class.getName());
        this.orderId = orderId;
        this.userId = userId;
        this.totalAmount = totalAmount;
    }

    public DoPaymentCommand() {
        super();
    }

    public String getOrderId() {
        return orderId;
    }

    public String getUserId() {
        return userId;
    }

    public long getTotalAmount() {
        return totalAmount;
    }
}
