package com.devy.orders.service;

import com.devy.orders.controller.request.PlaceOrderRequestDTO;
import com.devy.orders.controller.request.SearchOrderInfoRequestDTO;
import com.devy.orders.controller.response.SearchOrderInfoResponseDTO;

public interface OrderService {
    public String placeOrder(PlaceOrderRequestDTO request);
    public SearchOrderInfoResponseDTO searchOrder(SearchOrderInfoRequestDTO request);

    public void cancelOrder(String eventId);
    public void confirmOrder(String eventId);
}
