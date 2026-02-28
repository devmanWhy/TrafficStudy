package com.devy.trafficstudypart3.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/v1/threads")
@RestController
public class ThreadController {
    private Logger log = LoggerFactory.getLogger(ThreadController.class);

    @GetMapping
    public ResponseEntity<Void> getThreads() throws InterruptedException {
        log.info("Thread is : {}", Thread.currentThread().getName());
        return ResponseEntity.ok().build();
    }
}
