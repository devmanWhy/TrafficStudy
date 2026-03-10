package com.devy.products.repository.message;

import com.devy.common.event.EventInfo;
import com.devy.common.event.product.ProductEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Repository;
import tools.jackson.databind.ObjectMapper;

@Repository
public class ProductEventKafkaMessageRepository implements ProductEventMessageRepository {

    private Logger log = LoggerFactory.getLogger(this.getClass());
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public ProductEventKafkaMessageRepository(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public void publish(ProductEvent event) {
        log.info("Publishing event: {}", event);
        kafkaTemplate.send(EventInfo.PRODUCTS.PRODUCT_EVENT_TOPIC, objectMapper.writeValueAsString(event));
    }
}
