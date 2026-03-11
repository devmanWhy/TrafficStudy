package com.devy.orders.domain;

import com.devy.orders.domain.values.OrderStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;

import java.time.ZonedDateTime;

@Entity
public class Orders {

    @Id
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
}
