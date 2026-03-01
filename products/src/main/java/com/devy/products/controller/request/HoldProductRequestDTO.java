package com.devy.products.controller.request;

public record HoldProductRequestDTO(
        String productId,
        int quantity
) {
}
