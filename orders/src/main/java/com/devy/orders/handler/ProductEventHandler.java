package com.devy.orders.handler;

import com.devy.common.event.EventInfo;
import com.devy.common.event.product.InventoryReleasedEvent;
import com.devy.orders.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import static com.devy.common.event.EventInfo.PRODUCTS.INVENTORY_RELEASED_EVENT_CLASS;

@Component
public class ProductEventHandler {
    private Logger log = LoggerFactory.getLogger(this.getClass());

    private final ObjectMapper objectMapper;
    private final OrderService orderService;

    public ProductEventHandler(ObjectMapper objectMapper, OrderService orderService) {
        this.objectMapper = objectMapper;
        this.orderService = orderService;
    }

    @KafkaListener(topics = {EventInfo.PRODUCTS.PRODUCT_EVENT_TOPIC})
    public void handleProductEvent(String message, Acknowledgment acknowledgment) {
        JsonNode jsonNode = objectMapper.readTree(message);
        JsonNode eventId = jsonNode.get("eventId");
        JsonNode eventType = jsonNode.get("eventType");
        if (INVENTORY_RELEASED_EVENT_CLASS.getName().equals(eventType.asString())) {
            try {
                InventoryReleasedEvent inventoryReleasedEvent = objectMapper.readValue(message, INVENTORY_RELEASED_EVENT_CLASS);

                orderService.cancelOrder(inventoryReleasedEvent.getEventId());
                log.info("InventoryReleasedEvent: {}", inventoryReleasedEvent);
            } catch (Exception e) {

                log.error("Failed to pay inventoryReservedEvent: {}", message, e);
            }
        }

        acknowledgment.acknowledge();
    }
}
