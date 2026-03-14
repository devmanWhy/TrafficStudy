package com.devy.products.service;

import com.devy.products.controller.response.SearchProductInfoResponseDTO;

public interface ProductService {
    public void holdProduct(String productId, int quantity);
    public void holdProduct(String orderId, String productId, int quantity);
    public SearchProductInfoResponseDTO searchProductInfo(String productId);

    public void releaseInventory(String productId, int quantity);
    public void releaseInventory(String orderId, String productId, int quantity);
}
