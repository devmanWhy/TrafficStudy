package com.devy.payments.repository.message;

import com.devy.common.event.EventInfo;
import com.devy.common.event.payment.PaymentEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Repository;
import tools.jackson.databind.ObjectMapper;

@Repository
public class PaymentEventKafkaMessageRepository implements PaymentEventMessageRepository {
    private Logger log = LoggerFactory.getLogger(this.getClass());
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public PaymentEventKafkaMessageRepository(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public void publish(PaymentEvent event) {
        kafkaTemplate.send(EventInfo.PAYMENTS.PAYMENT_EVENT_TOPIC, objectMapper.writeValueAsString(event));
    }
}
