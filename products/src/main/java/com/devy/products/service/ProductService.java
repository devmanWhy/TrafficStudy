package com.devy.products.service;

import com.devy.products.controller.response.SearchProductInfoResponseDTO;

public interface ProductService {
    public void holdProduct(String productId, int quantity);
    public SearchProductInfoResponseDTO searchProductInfo(String productId);
}
