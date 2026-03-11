package com.devy.orders.controller.response;

import com.devy.orders.domain.values.OrderStatus;

import java.time.ZonedDateTime;

public record SearchOrderInfoResponseDTO(
        String orderId,
        String userId,
        String productId,
        String productName,
        int productPrice,
        int productQuantity,
        long paymentAmount,
        OrderStatus orderStatus,
        ZonedDateTime orderedAt,
        ZonedDateTime paidAt
) {
}
