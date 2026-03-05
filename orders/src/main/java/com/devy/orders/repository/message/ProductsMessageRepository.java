package com.devy.orders.repository.message;

public interface ProductsMessageRepository {
    public void holdProduct(String productId, int quantity);
}
