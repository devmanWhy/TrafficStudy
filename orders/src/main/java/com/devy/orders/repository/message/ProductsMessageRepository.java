package com.devy.orders.repository.message;

import com.devy.orders.controller.request.PlaceOrderRequestDTO;

public interface ProductsMessageRepository {
    public void holdProduct(String productId, int quantity);

    public void holdProduct(PlaceOrderRequestDTO requestDTO, String orderId);
}
