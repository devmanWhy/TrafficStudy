package com.devy.orderhistory.controller;

import com.devy.orderhistory.service.OrderHistoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping("/api/v1/order-history")
@RestController
public class OrderHistoryController {

    private final OrderHistoryService orderHistoryService;

    public OrderHistoryController(OrderHistoryService orderHistoryService) {
        this.orderHistoryService = orderHistoryService;
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<List<Object>> getOrderHistory(
            @PathVariable("orderId") String orderId
    ) {
        return ResponseEntity.ok(orderHistoryService.getOrderHistory(orderId));
    }
}
