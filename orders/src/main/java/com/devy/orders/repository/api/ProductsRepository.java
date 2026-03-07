package com.devy.orders.repository.api;

import com.devy.orders.repository.api.response.SearchProductInfoResponseDTO;

import java.util.concurrent.CompletableFuture;

public interface ProductsRepository {
    public String holdProduct(String productId, int quantity);
    CompletableFuture<String> asyncHoldProduct(String productId, int quantity);
    public SearchProductInfoResponseDTO searchProductInfo(String productId);
}
