package com.devy.orders.domain;

import com.devy.common.event.BaseCommand;
import com.devy.common.event.BaseEvent;
import com.devy.common.event.order.OrderCancelledEvent;
import com.devy.common.event.order.OrderConfirmedEvent;
import com.devy.common.event.order.OrderPlacedEvent;
import com.devy.common.event.order.command.CancelOrderCommand;
import com.devy.common.event.order.command.ConfirmOrderCommand;
import com.devy.common.event.sourcing.AbstractEventSource;
import com.devy.orders.domain.values.OrderStatus;
import jakarta.persistence.Enumerated;

import java.time.ZonedDateTime;

public class Orders extends AbstractEventSource {

    private String orderId;
    private String userId;
    private String productsId;
    private int quantity;
    private long totalAmount;
    private ZonedDateTime orderAt;
    @Enumerated(value = jakarta.persistence.EnumType.STRING)
    private OrderStatus orderStatus;


    public Orders() {
    }

    public Orders(String orderId, String userId, String productsId, int quantity, long totalAmount) {
        this(orderId, userId, productsId, quantity, totalAmount, ZonedDateTime.now(), OrderStatus.PENDING);
    }

    public Orders(String orderId, String userId, String productsId, int quantity, long totalAmount, ZonedDateTime orderAt, OrderStatus orderStatus) {
        this.orderId = orderId;
        this.userId = userId;
        this.productsId = productsId;
        this.quantity = quantity;
        this.totalAmount = totalAmount;
        this.orderAt = orderAt;
        this.orderStatus = orderStatus;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getUserId() {
        return userId;
    }

    public String getProductsId() {
        return productsId;
    }

    public int getQuantity() {
        return quantity;
    }

    public long getTotalAmount() {
        return totalAmount;
    }

    public ZonedDateTime getOrderAt() {
        return orderAt;
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public void cancel() {
        this.orderStatus = OrderStatus.CANCELLED;
    }

    public void confirm() {
        this.orderStatus = OrderStatus.COMPLETED;
    }

    @Override
    public String toString() {
        return "Orders{" +
                "orderId='" + orderId + '\'' +
                ", userId='" + userId + '\'' +
                ", productsId='" + productsId + '\'' +
                ", quantity=" + quantity +
                ", totalAmount=" + totalAmount +
                ", orderAt=" + orderAt +
                '}';
    }

    @Override
    public BaseEvent handleCommand(BaseCommand command) {
        BaseEvent event = null;
        if (command instanceof ConfirmOrderCommand confirmOrderCommand) {
            return new OrderConfirmedEvent(orderId);
        }

        if (command instanceof CancelOrderCommand cancelOrderCommand) {
            return new OrderCancelledEvent(orderId);
        }
        return event;
    }

    @Override
    public boolean handleEvent(BaseEvent event) {
        if (event instanceof OrderPlacedEvent orderPlacedEvent) {
            orderId = orderPlacedEvent.getOrderId();
            userId = orderPlacedEvent.getUserId();
            productsId = orderPlacedEvent.getProductId();
            quantity = orderPlacedEvent.getQuantity();
            totalAmount = orderPlacedEvent.getTotalAmount();
            orderStatus = OrderStatus.PENDING;
        }

        if (event instanceof OrderConfirmedEvent orderConfirmedEvent) {
            this.orderStatus = OrderStatus.COMPLETED;
        }

        if (event instanceof OrderCancelledEvent orderCancelledEvent) {
            this.orderStatus = OrderStatus.CANCELLED;
        }
        return true;
    }
}
