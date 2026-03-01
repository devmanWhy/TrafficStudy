package com.devy.products.controller;

import com.devy.common.ApiInfo;
import com.devy.products.controller.request.HoldProductRequestDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping(ApiInfo.PRODUCTS.BASE_PATH + "/products")
@RestController
public class ProductController {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    @PostMapping("/hold")
    public String holdProduct(@RequestBody HoldProductRequestDTO request) {
        log.info("Holding product: {}", request);
        return "Product held successfully";
    }
}
