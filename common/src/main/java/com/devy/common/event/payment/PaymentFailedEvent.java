package com.devy.common.event.payment;

public class PaymentFailedEvent extends PaymentEvent {
    private String userId;
    private String productId;
    private int quantity;
    private long totalAmount;

    public PaymentFailedEvent(String eventId, String userId, String productId, int quantity, long totalAmount) {
        super(eventId, PaymentFailedEvent.class.getName());
        this.userId = userId;
        this.productId = productId;
        this.quantity = quantity;
        this.totalAmount = totalAmount;
    }


    public PaymentFailedEvent() {
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
