package com.devy.products.controller;

import com.devy.common.ApiInfo;
import com.devy.products.controller.request.HoldProductRequestDTO;
import com.devy.products.controller.request.SearchProductInfoRequestDTO;
import com.devy.products.controller.response.HoldProductResponseDTO;
import com.devy.products.controller.response.SearchProductInfoResponseDTO;
import com.devy.products.service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping(ApiInfo.PRODUCTS.BASE_PATH + ApiInfo.PRODUCTS.PRODUCT_PATH)
@RestController
public class ProductController {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping("/hold")
    public HoldProductResponseDTO holdProduct(@RequestBody HoldProductRequestDTO request) {
        log.info("\uD83C\uDF81 REST : Product held successfully for : {}", request);
        productService.holdProduct(request.productId(), request.quantity());
        return new HoldProductResponseDTO("Held");
    }

    @PostMapping("/search")
    public ResponseEntity<SearchProductInfoResponseDTO> searchProductInfo(
            @RequestBody SearchProductInfoRequestDTO request
    ) {
        log.info("REST : Searching product info for productId: {}", request.productId());
        return ResponseEntity.ok(productService.searchProductInfo(request.productId()));
    }
}
