package com.devy.payments.controller.request;

public record SearchPaymentInfoRequestDTO(
        String orderId,
        String userId
) {
}
