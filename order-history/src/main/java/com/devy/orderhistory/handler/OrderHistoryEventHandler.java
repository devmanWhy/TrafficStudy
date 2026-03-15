package com.devy.orderhistory.handler;

import com.devy.common.event.EventInfo;
import com.devy.orderhistory.service.OrderHistoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@Component
public class OrderHistoryEventHandler {

    private Logger log = LoggerFactory.getLogger(this.getClass());
    private final ObjectMapper objectMapper;
    private final OrderHistoryService orderHistoryService;

    public OrderHistoryEventHandler(ObjectMapper objectMapper, OrderHistoryService orderHistoryService) {
        this.objectMapper = objectMapper;
        this.orderHistoryService = orderHistoryService;
    }

    @KafkaListener(topics = {EventInfo.ORDERS.ORDER_EVENT_TOPIC, EventInfo.PRODUCTS.PRODUCT_EVENT_TOPIC, EventInfo.PAYMENTS.PAYMENT_EVENT_TOPIC})
    public void handleEvent(String message, Acknowledgment acknowledgment) {
        JsonNode jsonNode = objectMapper.readTree(message);
        String orderId = jsonNode.get("orderId").asString();
        String eventType = jsonNode.get("eventType").asString();
        orderHistoryService.saveOrderHistory(orderId, eventType, message);
        acknowledgment.acknowledge();

    }

}
