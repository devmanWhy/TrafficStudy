package com.devy.products.eventHandler;

import com.devy.common.event.EventInfo;
import com.devy.common.event.order.OrderPlacedEvent;
import com.devy.products.domain.ProcessedEvent;
import com.devy.products.repository.jpa.ProcessedEventRepository;
import com.devy.products.service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.util.Optional;

import static com.devy.common.event.EventInfo.ORDERS.ORDER_PLACED_EVENT_CLASS;

@Component
public class OrderEventHandler {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    private final ObjectMapper objectMapper;
    private final ProductService productService;
    private final ProcessedEventRepository processedEventRepository;

    public OrderEventHandler(ObjectMapper objectMapper, ProductService productService, ProcessedEventRepository processedEventRepository) {
        this.objectMapper = objectMapper;
        this.productService = productService;
        this.processedEventRepository = processedEventRepository;
    }

    @KafkaListener(topics = {EventInfo.ORDERS.ORDER_EVENT_TOPIC})
    public void handleOrderEvent(String message, Acknowledgment acknowledgment) {
        JsonNode jsonNode = objectMapper.readTree(message);
        JsonNode eventId = jsonNode.get("eventId");
        JsonNode eventType = jsonNode.get("eventType");
        Optional<ProcessedEvent> existEventOptional = processedEventRepository.findById(
                new ProcessedEvent.ProcessedEventId(
                        "product-" + eventId.asString(), eventType.asString())
        );
        if (existEventOptional.isEmpty()) {

            if (ORDER_PLACED_EVENT_CLASS.getName().equals(eventType.asString())) {
                OrderPlacedEvent orderPlacedEvent = objectMapper.readValue(message, ORDER_PLACED_EVENT_CLASS);
                productService.holdProduct(orderPlacedEvent.getProductId(), orderPlacedEvent.getQuantity());
                log.info("OrderPlacedEvent: {}", orderPlacedEvent);
            }

            processedEventRepository.save(new ProcessedEvent(
                    "product-"+eventId.asString(),
                    eventType.asString(),
                    message
            ));
        }
        acknowledgment.acknowledge();
    }
}
