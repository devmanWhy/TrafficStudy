package com.devy.orders.repository.api.response;

import java.time.ZonedDateTime;

public record SearchPaymentInfoResponseDTO(
        String orderId,
        long amount,
        ZonedDateTime paidAt
) {
}
