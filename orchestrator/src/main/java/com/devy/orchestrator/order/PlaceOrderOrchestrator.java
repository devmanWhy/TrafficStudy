package com.devy.orchestrator.order;

import com.devy.common.event.BaseCommand;
import com.devy.common.event.BaseEvent;
import com.devy.common.event.EventInfo;
import com.devy.common.event.order.OrderPlacedEvent;
import com.devy.common.event.payment.PaymentFailedEvent;
import com.devy.common.event.payment.PaymentSucceedEvent;
import com.devy.common.event.product.InventoryReleasedEvent;
import com.devy.common.event.product.InventoryReservedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;

import static com.devy.common.event.EventInfo.ORDERS.ORDER_PLACED_EVENT_CLASS;
import static com.devy.common.event.EventInfo.PAYMENTS.PAYMENT_FAILED_EVENT_CLASS;
import static com.devy.common.event.EventInfo.PAYMENTS.PAYMENT_SUCCEED_EVENT_CLASS;
import static com.devy.common.event.EventInfo.PRODUCTS.INVENTORY_RELEASED_EVENT_CLASS;
import static com.devy.common.event.EventInfo.PRODUCTS.INVENTORY_RESERVED_EVENT_CLASS;

@Component
public class PlaceOrderOrchestrator {
    private Logger log = LoggerFactory.getLogger(this.getClass());
    private Map<String, CommandManager> commandManagers = new HashMap<>();
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public PlaceOrderOrchestrator(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }


    @KafkaListener(topics = {EventInfo.ORDERS.ORDER_EVENT_TOPIC})
    public void handleOrderEvent(String message, Acknowledgment acknowledgment) {
        JsonNode jsonNode = objectMapper.readTree(message);
        String eventId = jsonNode.get("eventId").asString();
        JsonNode eventType = jsonNode.get("eventType");
        if (ORDER_PLACED_EVENT_CLASS.getName().equals(eventType.asString())) {
            log.info("OrderPlacedEvent message : {}", message);
            OrderPlacedEvent orderPlacedEvent = objectMapper.treeToValue(jsonNode, ORDER_PLACED_EVENT_CLASS);
            commandManagers.put(orderPlacedEvent.getOrderId(), new CommandManager(orderPlacedEvent.getOrderId()));
            kafkaTemplate.send(EventInfo.PRODUCTS.PRODUCT_COMMAND_TOPIC, objectMapper.writeValueAsString(getCommand(orderPlacedEvent)));
        }
        acknowledgment.acknowledge();

    }

    @KafkaListener(topics = {EventInfo.PRODUCTS.PRODUCT_EVENT_TOPIC})
    public void handleProductEvent(String message, Acknowledgment acknowledgment) {
        JsonNode jsonNode = objectMapper.readTree(message);
        String eventId = jsonNode.get("eventId").asString();
        JsonNode eventType = jsonNode.get("eventType");
        if (INVENTORY_RESERVED_EVENT_CLASS.getName().equals(eventType.asString())) {
            // Event 를 받아서
            log.info("InventoryReservedEvent message : {}", message);
            InventoryReservedEvent inventoryReservedEvent = objectMapper.treeToValue(jsonNode, INVENTORY_RESERVED_EVENT_CLASS);
            // Command 를 보낸다
            kafkaTemplate.send(EventInfo.PAYMENTS.PAYMENT_COMMAND_TOPIC, objectMapper.writeValueAsString(getCommand(inventoryReservedEvent)));
        }
        if (INVENTORY_RELEASED_EVENT_CLASS.getName().equals(eventType.asString())) {
            log.info("InventoryReleasedEvent message : {}", message);
            InventoryReleasedEvent inventoryReleasedEvent = objectMapper.treeToValue(jsonNode, INVENTORY_RELEASED_EVENT_CLASS);
            kafkaTemplate.send(EventInfo.ORDERS.ORDER_COMMAND_TOPIC, objectMapper.writeValueAsString(getCommand(inventoryReleasedEvent)));
        }

        acknowledgment.acknowledge();

    }

    @KafkaListener(topics = {EventInfo.PAYMENTS.PAYMENT_EVENT_TOPIC})
    public void handlePaymentEvent(String message, Acknowledgment acknowledgment) {
        JsonNode jsonNode = objectMapper.readTree(message);
        String eventId = jsonNode.get("eventId").asString();
        JsonNode eventType = jsonNode.get("eventType");
        if (PAYMENT_SUCCEED_EVENT_CLASS.getName().equals(eventType.asString())) {
            log.info("PaymentSucceedEvent message : {}", message);
            PaymentSucceedEvent paymentSucceedEvent = objectMapper.treeToValue(jsonNode, PAYMENT_SUCCEED_EVENT_CLASS);
            kafkaTemplate.send(EventInfo.ORDERS.ORDER_COMMAND_TOPIC, objectMapper.writeValueAsString(getCommand(paymentSucceedEvent)));
        }

        if (PAYMENT_FAILED_EVENT_CLASS.getName().equals(eventType.asString())) {
            log.info("PaymentFailedEvent message : {}", message);
            PaymentFailedEvent paymentFailedEvent = objectMapper.treeToValue(jsonNode, PAYMENT_FAILED_EVENT_CLASS);
            kafkaTemplate.send(EventInfo.PRODUCTS.PRODUCT_COMMAND_TOPIC, objectMapper.writeValueAsString(getCommand(paymentFailedEvent)));
        }

        acknowledgment.acknowledge();
    }

    private BaseCommand getCommand(BaseEvent event) {
        CommandManager commandManager = commandManagers.get(event.getEventId());
        commandManager.apply(event);
        return commandManager.getCommand();
    }


}
