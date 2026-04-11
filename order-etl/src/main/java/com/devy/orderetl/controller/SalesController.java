package com.devy.orderetl.controller;

import com.devy.orderetl.domain.Sales;
import com.devy.orderetl.service.SalesService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/v1/sales")
@RestController
public class SalesController {

    private final SalesService salesService;

    public SalesController(SalesService salesService) {
        this.salesService = salesService;
    }

    @GetMapping("/{date}")
    public Sales getSalesByDate(@PathVariable String date) {
        return salesService.getSalesByDate(date);
    }

}
