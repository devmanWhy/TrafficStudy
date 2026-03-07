package com.devy.payments.controller.request;

public record DoPaymentRequestDTO(
        String orderId,
        String userId,
        long amount
) {
}
