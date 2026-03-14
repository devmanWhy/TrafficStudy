package com.devy.orders.handler;

import com.devy.common.event.EventInfo;
import com.devy.common.event.order.command.CancelOrderCommand;
import com.devy.common.event.order.command.ConfirmOrderCommand;
import com.devy.orders.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import static com.devy.common.event.EventInfo.ORDERS.CANCEL_ORDER_COMMAND_CLASS;
import static com.devy.common.event.EventInfo.ORDERS.CONFIRM_ORDER_COMMAND_CLASS;

@Component
public class OrderCommandHandler {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    private final ObjectMapper objectMapper;
    private final OrderService orderService;
    private final KafkaTemplate<String, String> kafkaTemplate;

    public OrderCommandHandler(ObjectMapper objectMapper, OrderService orderService, KafkaTemplate<String, String> kafkaTemplate) {
        this.objectMapper = objectMapper;
        this.orderService = orderService;
        this.kafkaTemplate = kafkaTemplate;
    }

    @KafkaListener(topics = {EventInfo.ORDERS.ORDER_COMMAND_TOPIC})
    public void handleOrderCommand(String message, Acknowledgment acknowledgment) {
        log.info("Received message: {}", message);
        JsonNode jsonNode = objectMapper.readTree(message);
        JsonNode eventId = jsonNode.get("eventId");
        JsonNode eventType = jsonNode.get("eventType");
        if (CANCEL_ORDER_COMMAND_CLASS.getName().equals(eventType.asString())) {
            try {
                CancelOrderCommand cancelOrderCommand = objectMapper.treeToValue(jsonNode, CANCEL_ORDER_COMMAND_CLASS);
                orderService.cancelOrder(cancelOrderCommand.getOrderId());
                log.info("CancelOrderCommand: {}", cancelOrderCommand);
            } catch (Exception e) {

                log.error("Failed to pay inventoryReservedEvent: {}", message, e);
            }
        }

        if (CONFIRM_ORDER_COMMAND_CLASS.getName().equals(eventType.asString())) {
            try {
                ConfirmOrderCommand confirmOrderCommand = objectMapper.treeToValue(jsonNode, CONFIRM_ORDER_COMMAND_CLASS);
                orderService.confirmOrder(confirmOrderCommand.getOrderId());
                log.info("ConfirmOrderCommand: {}", confirmOrderCommand);
            } catch (Exception e) {

                log.error("Failed to pay inventoryReservedEvent: {}", message, e);
            }
        }
        acknowledgment.acknowledge();
    }
}
