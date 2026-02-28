package com.devy.trafficstudypart3.controller;

import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/v1/ratelimiter")
@RestController
public class RateLimiterController {

    @RateLimiter(name = "getRateLimiter")
    @GetMapping("/getRateLimiter")
    public String getRateLimiter() {
        return "OK";
    }

    @RateLimiter(name = "customRateLimiter", fallbackMethod = "fallback")
    @GetMapping("/getRateLimiter2")
    public String getRateLimiter2() {
        return "OK";
    }

    public String fallback(Throwable throwable) {
        throwable.printStackTrace();
        return "Fallback";
    }
}
