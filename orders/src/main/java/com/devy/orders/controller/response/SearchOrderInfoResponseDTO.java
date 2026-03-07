package com.devy.orders.controller.response;

import java.time.ZonedDateTime;

public record SearchOrderInfoResponseDTO(
        String orderId,
        String userId,
        String productId,
        String productName,
        int productPrice,
        int productQuantity,
        long paymentAmount,
        ZonedDateTime orderedAt,
        ZonedDateTime paidAt
) {
}
