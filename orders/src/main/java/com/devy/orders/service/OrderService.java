package com.devy.orders.service;

import com.devy.orders.controller.request.PlaceOrderRequestDTO;

public interface OrderService {
    public String placeOrder(PlaceOrderRequestDTO request);
}
