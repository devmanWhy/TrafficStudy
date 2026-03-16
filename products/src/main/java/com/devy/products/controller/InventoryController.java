package com.devy.products.controller;

import com.devy.products.domain.Inventory;
import com.devy.products.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/v1/inventory")
@RestController
public class InventoryController {

    private final ProductService productService;

    public InventoryController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("{productId}")
    public ResponseEntity<Inventory> getInventoryById(
            @PathVariable("productId") String productId
    ) {
        return ResponseEntity.ok(productService.getInventory(productId));
    }
}
