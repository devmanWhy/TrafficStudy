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
        if (EventInfo.ORDERS.ORDER_PLACED_EVENT_CLASS.getName().equals(eventType.asString())) {
            OrderPlacedEvent event = objectMapper.readValue(message, OrderPlacedEvent.class);
            log.info("Received OrderPlacedEvent : {}", event);
            paymentService.pay(event.getEventId(), event.getUserId(), event.getTotalAmount());

        }
        processedEventRepository.save(new ProcessedEvent(
                eventId.asString(),
                eventType.asString(),
                message
        ));
        acknowledgment.acknowledge();
    }
}
