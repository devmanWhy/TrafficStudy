package com.devy.payments.handler;

import com.devy.common.event.EventInfo;
import com.devy.common.event.payment.PaymentFailedEvent;
import com.devy.common.event.product.InventoryReservedEvent;
import com.devy.payments.domain.ProcessedEvent;
import com.devy.payments.repository.jpa.ProcessedEventRepository;
import com.devy.payments.repository.message.PaymentEventMessageRepository;
import com.devy.payments.service.PaymentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.util.Optional;

import static com.devy.common.event.EventInfo.PRODUCTS.INVENTORY_RESERVED_EVENT_CLASS;

@Component
public class ProductEventHandler {
    private Logger log = LoggerFactory.getLogger(this.getClass());

    private final ObjectMapper objectMapper;
    private final PaymentService paymentService;
    private final ProcessedEventRepository processedEventRepository;
    private final PaymentEventMessageRepository paymentEventMessageRepository;

    public ProductEventHandler(ObjectMapper objectMapper, PaymentService paymentService, ProcessedEventRepository processedEventRepository, PaymentEventMessageRepository paymentEventMessageRepository) {
        this.objectMapper = objectMapper;
        this.paymentService = paymentService;
        this.processedEventRepository = processedEventRepository;
        this.paymentEventMessageRepository = paymentEventMessageRepository;
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

                try {
                    paymentService.pay(
                            inventoryReservedEvent.getEventId(),
                            inventoryReservedEvent.getUserId(),
                            inventoryReservedEvent.getTotalAmount()
                    );
                    log.info("InventoryReservedEvent: {}", inventoryReservedEvent);
                } catch (Exception e) {
                    paymentEventMessageRepository.publish(
                            new PaymentFailedEvent(
                                    inventoryReservedEvent.getEventId(),
                                    inventoryReservedEvent.getUserId(),
                                    inventoryReservedEvent.getProductId(),
                                    inventoryReservedEvent.getQuantity(),
                                    inventoryReservedEvent.getTotalAmount()
                            )
                    );
                    log.info("Failed to pay inventoryReservedEvent: {}", message, e);
                }
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
