package com.devy.orders.controller.request;

public record SearchOrderInfoRequestDTO(
        String orderId,
        String userId
) {
}
