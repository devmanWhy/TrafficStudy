package com.devy.products.controller.response;

public record SearchProductInfoResponseDTO(
        String productId,
        String name,
        String description,
        int price
) {
}
