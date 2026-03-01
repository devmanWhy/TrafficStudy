package com.devy.orders.service;

import com.devy.orders.controller.request.PlaceOrderRequestDTO;
import com.devy.orders.repository.api.ProductsRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class OrderServiceImpl implements OrderService {

    private final ProductsRepository productsRestRepository;

    public OrderServiceImpl(ProductsRepository productsGrpcRepository) {
        this.productsRestRepository = productsGrpcRepository;
    }

    @Override
    public String placeOrder(PlaceOrderRequestDTO request) {
        String orderId = UUID.randomUUID().toString().replace("-", "");
        productsRestRepository.holdProduct(request.productId(), request.quantity());
        return orderId;
    }
}
