package com.devy.trafficstudypart3.service;

import org.springframework.stereotype.Service;

@Service
public class StubCircuitService implements CircuitService {
    @Override
    public void logic(String code) {
        if (code.equals("fail")) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            throw new RuntimeException("Other Service is abnormal");
        }
    }
}
