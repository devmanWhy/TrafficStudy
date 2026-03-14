package com.devy.products.handler;

import com.devy.common.event.EventInfo;
import com.devy.common.event.product.InventoryReleasedEvent;
import com.devy.common.event.product.InventoryReservedEvent;
import com.devy.common.event.product.command.ReleaseInventoryCommand;
import com.devy.common.event.product.command.ReserveInventoryCommand;
import com.devy.products.service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import static com.devy.common.event.EventInfo.PRODUCTS.RELEASE_INVENTORY_COMMAND_CLASS;
import static com.devy.common.event.EventInfo.PRODUCTS.RESERVE_INVENTORY_COMMAND_CLASS;

@Component
public class ProductCommandHandler {
    private Logger log = LoggerFactory.getLogger(this.getClass());

    private final ObjectMapper objectMapper;
    private final ProductService productService;
    private final KafkaTemplate<String, String> kafkaTemplate;

    public ProductCommandHandler(ObjectMapper objectMapper, ProductService productService, KafkaTemplate<String, String> kafkaTemplate) {
        this.objectMapper = objectMapper;
        this.productService = productService;
        this.kafkaTemplate = kafkaTemplate;
    }

    @KafkaListener(topics = {EventInfo.PRODUCTS.PRODUCT_COMMAND_TOPIC})
    public void handleProductCommand(String message, Acknowledgment acknowledgment) {
        log.info("Received message: {}", message);
        JsonNode jsonNode = objectMapper.readTree(message);
        JsonNode eventId = jsonNode.get("eventId");
        JsonNode eventType = jsonNode.get("eventType");
        if (RESERVE_INVENTORY_COMMAND_CLASS.getName().equals(eventType.asString())) {
            try {
                ReserveInventoryCommand reserveInventoryCommand = objectMapper.treeToValue(jsonNode, RESERVE_INVENTORY_COMMAND_CLASS);
                productService.holdProduct(reserveInventoryCommand.getProductId(), reserveInventoryCommand.getQuantity());
                log.info("ReserveInventoryCommand: {}", reserveInventoryCommand);
                InventoryReservedEvent inventoryReservedEvent = new InventoryReservedEvent(
                        reserveInventoryCommand.getOrderId(),
                        reserveInventoryCommand.getProductId(),
                        reserveInventoryCommand.getQuantity()
                );
                kafkaTemplate.send(EventInfo.PRODUCTS.PRODUCT_EVENT_TOPIC,
                        objectMapper.writeValueAsString(inventoryReservedEvent));
            } catch (Exception e) {

                log.error("Failed reserveInventoryCommand: {}", message, e);
            }
        }

        if (RELEASE_INVENTORY_COMMAND_CLASS.getName().equals(eventType.asString())) {
            try {
                ReleaseInventoryCommand releaseInventoryCommand = objectMapper.treeToValue(jsonNode, RELEASE_INVENTORY_COMMAND_CLASS);
                productService.releaseInventory(releaseInventoryCommand.getProductId(), releaseInventoryCommand.getQuantity());
                log.info("ReleaseInventoryCommand: {}", releaseInventoryCommand);
                InventoryReleasedEvent inventoryReleasedEvent = new InventoryReleasedEvent(
                        releaseInventoryCommand.getOrderId(),
                        releaseInventoryCommand.getProductId(),
                        releaseInventoryCommand.getQuantity()
                );
                kafkaTemplate.send(EventInfo.PRODUCTS.PRODUCT_EVENT_TOPIC,
                        objectMapper.writeValueAsString(inventoryReleasedEvent));
            } catch (Exception e) {

                log.error("Failed to releaseInventoryCommand: {}", message, e);
            }
        }
        acknowledgment.acknowledge();
    }
}
