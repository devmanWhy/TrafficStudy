package com.devy.orders.repository.message;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Repository;
import tools.jackson.databind.ObjectMapper;

import java.util.HashMap;

@Repository
public class ProductsKafkaMessageRepository implements ProductsMessageRepository {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public ProductsKafkaMessageRepository(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public void holdProduct(String productId, int quantity) {
        kafkaTemplate.send(new ProducerRecord<>("orderEvent", null,
                objectMapper.writeValueAsString(
                        new HashMap<String, Object>() {{
                            put("productId", productId);
                            put("quantity", quantity);
                        }}
                )));
    }
}
