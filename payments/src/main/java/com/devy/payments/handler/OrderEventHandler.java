package com.devy.payments.handler;

import com.devy.common.event.EventInfo;
import com.devy.common.event.order.OrderPlacedEvent;
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

@Component
public class OrderEventHandler {
    private Logger log = LoggerFactory.getLogger(this.getClass());

    private final ObjectMapper objectMapper;
    private final PaymentService paymentService;
    private final ProcessedEventRepository processedEventRepository;

    public OrderEventHandler(ObjectMapper objectMapper, PaymentService paymentService, ProcessedEventRepository processedEventRepository) {
        this.objectMapper = objectMapper;
        this.paymentService = paymentService;
        this.processedEventRepository = processedEventRepository;
    }

    @KafkaListener(topics = {EventInfo.ORDERS.ORDER_EVENT_TOPIC})
    public void handleOrderEvent(String message, Acknowledgment acknowledgment) {
        JsonNode jsonNode = objectMapper.readTree(message);
        JsonNode eventId = jsonNode.get("eventId");
        JsonNode eventType = jsonNode.get("eventType");
        Optional<ProcessedEvent> existEventOptional = processedEventRepository.findById(
                new ProcessedEvent.ProcessedEventId(
                        "payment-" + eventId.asString(), eventType.asString())
        );
        if (existEventOptional.isEmpty()) {
            if (ORDER_PLACED_EVENT_CLASS.getName().equals(eventType.asString())) {
                OrderPlacedEvent orderPlacedEvent = objectMapper.readValue(message, ORDER_PLACED_EVENT_CLASS);
                paymentService.pay(
                        orderPlacedEvent.getEventId(),
                        orderPlacedEvent.getUserId(),
                        orderPlacedEvent.getTotalAmount()
                );
                log.info("OrderPlacedEvent: {}", orderPlacedEvent);
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
