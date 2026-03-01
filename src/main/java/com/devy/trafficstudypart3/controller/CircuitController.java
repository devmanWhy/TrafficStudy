package com.devy.trafficstudypart3.controller;

import com.devy.trafficstudypart3.service.CircuitService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/v1/circuit")
@RestController
public class CircuitController {

    private final CircuitService circuitService;

    public CircuitController(CircuitService circuitService) {
        this.circuitService = circuitService;
    }

    private Logger log = LoggerFactory.getLogger(this.getClass());

    @CircuitBreaker(name = "circuit", fallbackMethod = "fallback")
    @GetMapping
    public String getCircuit(
            @RequestParam("code") String code
    ) {
        log.info("Circuit is : {}", Thread.currentThread().getName());
        circuitService.logic(code);
        return "OK";
    }

    public String fallback(String code, Throwable throwable) {
        throwable.printStackTrace();
        return "Fallback : Service is abnormal for code : " + code + "";
    }
}
