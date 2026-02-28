package com.devy.trafficstudypart3.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/v1/simulation")
@RestController
public class SimulationController {

    private Logger log = LoggerFactory.getLogger(SimulationController.class);

    @GetMapping
    public String getSimulation(
            @RequestParam("duration") int duration,
            @RequestParam("size") int size
    ) throws InterruptedException {
        log.info("Simulation is : {}", Thread.currentThread().getName());
        int megabytes = size * 1024 * 1024;
        byte[] bytes = new byte[megabytes];
        Thread.sleep(duration);

        return "OK";
    }

}
