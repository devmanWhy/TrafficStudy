package com.devy.orders.repository.api;

import java.util.concurrent.CompletableFuture;

public interface ProductsRepository {
    public String holdProduct(String productId, int quantity);
    CompletableFuture<String> asyncHoldProduct(String productId, int quantity);
}
