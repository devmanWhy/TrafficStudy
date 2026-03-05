package com.devy.products.eventHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Component
public class StudyTopicEventHandler {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    @KafkaListener(topics = {"study-topic"})
    public void handleMessage(String message, Acknowledgment acknowledgment) {
        log.info("Received message : {}", message);
        acknowledgment.acknowledge();
    }
}
