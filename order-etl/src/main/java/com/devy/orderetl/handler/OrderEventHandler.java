package com.devy.orderetl.handler;

import com.devy.common.event.EventInfo;
import com.devy.common.event.order.OrderCancelledEvent;
import com.devy.common.event.order.OrderConfirmedEvent;
import com.devy.common.event.order.OrderEvent;
import com.devy.common.event.order.OrderPlacedEvent;
import com.devy.orderetl.service.SalesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.json.JsonMapper;

import java.util.Arrays;
import java.util.List;

@Component
public class OrderEventHandler {

    private Logger log = LoggerFactory.getLogger(this.getClass());
    private final ObjectMapper objectMapper;
    private final SalesService salesService;
    private final ObjectMapper cdcObjectMapper;

    public OrderEventHandler(ObjectMapper objectMapper, SalesService salesService) {
        this.objectMapper = objectMapper;
        this.salesService = salesService;
        this.cdcObjectMapper = JsonMapper.builder()
                // 1. 네이밍 전략 (Snake Case)
                .propertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE)
                //DTO에 없는 필드가 JSON에 있어도 에러 내지 않음
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .build();
    }

    @KafkaListener(topics = {EventInfo.ORDER_TOPIC})
    public void handleEvent(String message, Acknowledgment acknowledgment) {
        JsonNode jsonNode = cdcObjectMapper.readTree(message);
        String operation = jsonNode.get("__op").asString();
        // 초기에 DB 스냅샷 과정을 거칠때 => r, Insert -> c
        List<String> operationList = Arrays.asList("r", "c");
        if (operationList.contains(operation)) {
            String eventType = jsonNode.get("event_type").asString();
            String eventPayLoad = jsonNode.get("event_payload").asString();
            OrderEvent orderEvent = getOrderEvent(eventType, eventPayLoad);
            if(orderEvent != null) {
                salesService.updateSales(orderEvent);
            }
        }
        acknowledgment.acknowledge();
    }

    public OrderEvent getOrderEvent(String eventType, String eventPayload) {
        if (OrderPlacedEvent.class.getName().equals(eventType)) {
            return objectMapper.readValue(eventPayload, OrderPlacedEvent.class);
        }
        if (OrderConfirmedEvent.class.getName().equals(eventType)) {
            return objectMapper.readValue(eventPayload, OrderConfirmedEvent.class);
        }
        if (OrderCancelledEvent.class.getName().equals(eventType)) {
            return objectMapper.readValue(eventPayload, OrderCancelledEvent.class);
        }
        return null;
    }


}
