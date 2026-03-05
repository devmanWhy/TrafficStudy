package com.devy.orders.service;

import com.devy.orders.controller.request.PlaceOrderRequestDTO;
import com.devy.orders.repository.api.ProductsRepository;
import com.devy.orders.repository.message.ProductsMessageRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class OrderServiceImpl implements OrderService {

    private final ProductsMessageRepository productsRepository;

    public OrderServiceImpl(ProductsMessageRepository productsRepository) {
        this.productsRepository = productsRepository;
    }

    @Override
    public String placeOrder(PlaceOrderRequestDTO request) {
        String orderId = UUID.randomUUID().toString().replace("-", "");
        productsRepository.holdProduct(request.productId(), request.quantity());
        return orderId;
    }
}
