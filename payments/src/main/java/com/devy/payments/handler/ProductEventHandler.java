package com.devy.payments.handler;

import com.devy.common.event.EventInfo;
import com.devy.common.event.order.OrderPlacedEvent;
import com.devy.common.event.product.InventoryReservedEvent;
import com.devy.payments.domain.ProcessedEvent;
import com.devy.payments.repository.ProcessedEventRepository;
import com.devy.payments.service.PaymentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.util.Optional;

import static com.devy.common.event.EventInfo.ORDERS.ORDER_PLACED_EVENT_CLASS;
import static com.devy.common.event.EventInfo.PRODUCTS.INVENTORY_RESERVED_EVENT_CLASS;

@Component
public class ProductEventHandler {
    private Logger log = LoggerFactory.getLogger(this.getClass());

    private final ObjectMapper objectMapper;
    private final PaymentService paymentService;
    private final ProcessedEventRepository processedEventRepository;

    public ProductEventHandler(ObjectMapper objectMapper, PaymentService paymentService, ProcessedEventRepository processedEventRepository) {
        this.objectMapper = objectMapper;
        this.paymentService = paymentService;
        this.processedEventRepository = processedEventRepository;
    }

    @KafkaListener(topics = {EventInfo.PRODUCTS.PRODUCT_EVENT_TOPIC})
    public void handleProductEvent(String message, Acknowledgment acknowledgment) {
        JsonNode jsonNode = objectMapper.readTree(message);
        JsonNode eventId = jsonNode.get("eventId");
        JsonNode eventType = jsonNode.get("eventType");
        Optional<ProcessedEvent> existEventOptional = processedEventRepository.findById(
                new ProcessedEvent.ProcessedEventId(
                        "payment-" + eventId.asString(), eventType.asString())
        );
        if (existEventOptional.isEmpty()) {
            if (INVENTORY_RESERVED_EVENT_CLASS.getName().equals(eventType.asString())) {
                InventoryReservedEvent inventoryReservedEvent = objectMapper.readValue(message, INVENTORY_RESERVED_EVENT_CLASS);
                paymentService.pay(
                        inventoryReservedEvent.getEventId(),
                        inventoryReservedEvent.getUserId(),
                        inventoryReservedEvent.getTotalAmount()
                );
                log.info("InventoryReservedEvent: {}", inventoryReservedEvent);
            }

            processedEventRepository.save(new ProcessedEvent(
                    "payment-" + eventId.asString(),
                    eventType.asString(),
                    message
            ));
        }
        acknowledgment.acknowledge();
    }
}
