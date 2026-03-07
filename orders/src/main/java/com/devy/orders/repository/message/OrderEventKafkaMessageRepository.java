package com.devy.orders.repository.message;

import com.devy.common.event.EventInfo;
import com.devy.common.event.order.OrderEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Repository;
import tools.jackson.databind.ObjectMapper;

@Repository
public class OrderEventKafkaMessageRepository implements OrderEventMessageRepository {

    private Logger log = LoggerFactory.getLogger(this.getClass());
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public OrderEventKafkaMessageRepository(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public void publish(OrderEvent event) {
        log.info("Publishing event: {}", event);
        kafkaTemplate.send(EventInfo.ORDERS.ORDER_EVENT_TOPIC, objectMapper.writeValueAsString(event));
    }
}
