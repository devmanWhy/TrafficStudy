package com.devy.products.handler;

import com.devy.common.event.EventInfo;
import com.devy.common.event.product.InventoryCreatedEvent;
import com.devy.common.event.product.InventoryReleasedEvent;
import com.devy.common.event.product.InventoryReservedEvent;
import com.devy.common.event.product.ProductEvent;
import com.devy.products.controller.response.SearchProductInfoResponseDTO;
import com.devy.products.domain.Inventory;
import com.devy.products.domain.ProcessedEvent;
import com.devy.products.domain.Product;
import com.devy.products.domain.ProductInventory;
import com.devy.products.repository.jpa.ProcessedEventRepository;
import com.devy.products.repository.jpa.ProductInventoryRepository;
import com.devy.products.service.ProductService;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;
import tools.jackson.databind.DeserializationFeature;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.PropertyNamingStrategies;
import tools.jackson.databind.json.JsonMapper;

import java.util.Optional;

@Component
public class ProductEventHandler {

    private Logger log = LoggerFactory.getLogger(this.getClass());
    private final ProcessedEventRepository processedEventRepository;
    private final ProductInventoryRepository productInventoryRepository;
    private final ProductService productService;
    private final ObjectMapper objectMapper;
    private final ObjectMapper cdcObjectMapper;

    public ProductEventHandler(ProcessedEventRepository processedEventRepository, ProductInventoryRepository productInventoryRepository, ProductService productService, ObjectMapper objectMapper) {
        this.processedEventRepository = processedEventRepository;
        this.productInventoryRepository = productInventoryRepository;
        this.productService = productService;
        this.objectMapper = objectMapper;
        this.cdcObjectMapper = JsonMapper.builder()
                // 1. 네이밍 전략 (Snake Case)
                .propertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE)
                //DTO에 없는 필드가 JSON에 있어도 에러 내지 않음
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .build();
    }

    @KafkaListener(topics = {EventInfo.PRODUCTS.PRODUCT_EVENT_TOPIC})
    public void handleEvent(String message, Acknowledgment acknowledgment) {
        JsonNode node = objectMapper.readTree(message);
        String eventId = node.get("eventId").asString();
        String eventType = node.get("eventType").asString();
        ProcessedEvent.ProcessedEventId processedEventId = new ProcessedEvent.ProcessedEventId(
                eventId,
                eventType
        );
        Optional<ProcessedEvent> existEventOptional = processedEventRepository.findById(processedEventId);
        if (existEventOptional.isEmpty()) {
            log.info("Received event {}", message);
            String productId = node.get("productId").asString();
            // Inventory 데이터 가져오기 -> Inventory 에 해당하는 이벤트를 가져와서 Replay (Apply Event)
            Inventory inventory = productService.getInventory(productId);
            // Product 데이터 가져오기
            SearchProductInfoResponseDTO searchProductInfoResponseDTO = productService.searchProductInfo(productId);
            // Inventory + Product 데이터 저장
            ProductInventory productInventory = new ProductInventory(
                    inventory.getProductsId(),
                    searchProductInfoResponseDTO.name(),
                    searchProductInfoResponseDTO.description(),
                    inventory.getQuantity()
            );
            productInventoryRepository.save(productInventory);
            processedEventRepository.save(
                    new ProcessedEvent(
                            eventId,
                            eventType,
                            message
                    )
            );


        }

        acknowledgment.acknowledge();
    }

    @Transactional
    @KafkaListener(topics = {EventInfo.PRODUCT_TOPIC})
    public void handleProductUpdateEvent(String message, Acknowledgment acknowledgment) {
        log.info("CDC 로 부터 이벤트 수신 : {}", message);
        JsonNode jsonNode = cdcObjectMapper.readTree(message);
        String operation = jsonNode.get("__op").asString();
        if ("u".equals(operation)) {
            log.info("Product 업데이트 따라잡기 시작");
            Product product = cdcObjectMapper.treeToValue(jsonNode, Product.class);
            productInventoryRepository.findByProductsId(product.getProductsId())
                    .ifPresent(productInventory -> {
                        productInventory.setProductDescription(product.getDescription());
                    });
        }
        acknowledgment.acknowledge();
    }
}
