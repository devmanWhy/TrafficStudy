package com.devy.orders.controller;

import com.devy.common.ApiInfo;
import com.devy.orders.controller.request.PlaceOrderRequestDTO;
import com.devy.orders.controller.response.PlaceOrderResponseDTO;
import com.devy.orders.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping(ApiInfo.ORDERS.BASE_PATH + "/orders")
@RestController
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/place")
    public ResponseEntity<PlaceOrderResponseDTO> placeOrder(@RequestBody PlaceOrderRequestDTO request) {
        String orderId = orderService.placeOrder(request);
        return ResponseEntity.ok(new PlaceOrderResponseDTO(orderId, "OK"));
    }
}
