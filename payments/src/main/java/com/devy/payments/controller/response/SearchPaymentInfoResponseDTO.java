package com.devy.payments.controller.response;

import java.time.ZonedDateTime;

public record SearchPaymentInfoResponseDTO(
        String orderId,
        long amount,
        ZonedDateTime paidAt
) {
}
