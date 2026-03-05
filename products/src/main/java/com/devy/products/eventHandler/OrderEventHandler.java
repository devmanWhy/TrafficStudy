package com.devy.products.eventHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
public class OrderEventHandler {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    @KafkaListener(topics = {"orderEvent"})
    public void handleOrderEvent(String message, Acknowledgment acknowledgment) {
        log.info("\uD83C\uDF81 EVENT : Product held successfully for : {}", message);
        acknowledgment.acknowledge();
    }
}
