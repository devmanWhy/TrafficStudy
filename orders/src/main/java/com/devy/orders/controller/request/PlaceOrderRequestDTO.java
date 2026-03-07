package com.devy.orders.controller.request;

public record PlaceOrderRequestDTO(
        String productId,
        int quantity,
        String userId,
        Long totalAmount
) {
}
