package com.devy.orders.handler;

import com.devy.common.event.EventInfo;
import com.devy.orders.domain.Outbox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import tools.jackson.databind.*;
import tools.jackson.databind.json.JsonMapper;

import java.util.List;

import static com.devy.common.event.EventInfo.OUTBOX_TOPIC;

@Component
public class OutboxHandler {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    private final ObjectMapper objectMapper;
    private final KafkaTemplate<String, String> kafkaTemplate;

    public OutboxHandler(KafkaTemplate<String, String> kafkaTemplate) {
        this.objectMapper = JsonMapper.builder()
                // 1. 네이밍 전략 (Snake Case)
                .propertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE)
                //DTO에 없는 필드가 JSON에 있어도 에러 내지 않음
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .build();
        this.kafkaTemplate = kafkaTemplate;
    }


    @KafkaListener(topics = {OUTBOX_TOPIC})
    public void handleEvent(String message, Acknowledgment ack) {
        log.info("CDC 로 부터 이벤트 수신 : {}", message);
        JsonNode jsonNode = objectMapper.readTree(message);
        String operation = jsonNode.get("__op").asString();
        if("c".equals(operation)) {
            Outbox outbox = objectMapper.treeToValue(jsonNode, Outbox.class);
            if(isOrderEvent(outbox.getEventType())) {
                log.info("Processing order event: {}", outbox);
                kafkaTemplate.send(EventInfo.ORDERS.ORDER_EVENT_TOPIC, outbox.getEventPayload());
            }
        }
        ack.acknowledge();
    }

    private boolean isOrderEvent(String eventType) {
        return EventInfo.ORDERS.ORDER_CONFIRMED_EVENT_CLASS.getName().equals(eventType)
                || EventInfo.ORDERS.ORDER_CANCELLED_EVENT_CLASS.getName().equals(eventType)
                || EventInfo.ORDERS.ORDER_PLACED_EVENT_CLASS.getName().equals(eventType);
    }
}
