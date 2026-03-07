package com.devy.orders.repository.api.response;

public record SearchProductInfoResponseDTO(
        String productId,
        String name,
        String description,
        int price
) {
}
