package com.devy.products.service;

import com.devy.common.event.EventInfo;
import com.devy.common.event.product.InventoryCreatedEvent;
import com.devy.common.event.product.InventoryReleasedEvent;
import com.devy.common.event.product.InventoryReservedEvent;
import com.devy.common.event.product.ProductEvent;
import com.devy.common.event.product.command.ReserveInventoryCommand;
import com.devy.products.controller.response.SearchProductInfoResponseDTO;
import com.devy.products.domain.*;
import com.devy.products.repository.jpa.InventoryEventEntityRepository;
import com.devy.products.repository.jpa.InventoryRepository;
import com.devy.products.repository.jpa.OutboxRepository;
import com.devy.products.repository.jpa.ProductRepository;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Optional;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final InventoryRepository inventoryRepository;
    private final OutboxRepository outboxRepository;
    private final ObjectMapper objectMapper;
    private final InventoryEventEntityRepository inventoryEventEntityRepository;

    private Logger log = LoggerFactory.getLogger(this.getClass());

    public ProductServiceImpl(ProductRepository productRepository, InventoryRepository inventoryRepository, OutboxRepository outboxRepository, ObjectMapper objectMapper, InventoryEventEntityRepository inventoryEventEntityRepository) {
        this.productRepository = productRepository;
        this.inventoryRepository = inventoryRepository;
        this.outboxRepository = outboxRepository;
        this.objectMapper = objectMapper;
        this.inventoryEventEntityRepository = inventoryEventEntityRepository;
    }

    @PostConstruct
    public void init() {
//        for (int index = 0; index < 10; index++) {
//            Product product = new Product(
//                    "product-" + index,
//                    "상품 " + index,
//                    "정말 좋은 상품.",
//                    10000 + index
//            );
//            CreateInventoryCommand createInventoryCommand = new CreateInventoryCommand(
//                    UUID.randomUUID().toString(),
//                    product.getProductsId(),
//                    100_000
//            );
//            productRepository.save(product);
//            Inventory inventory = new Inventory();
//            inventory.applyCommand(createInventoryCommand);
//            inventory.getUnCommitedEvents().forEach(productEvent -> {
//                inventoryEventEntityRepository.save(
//                        new InventoryEventEntity(
//                                createInventoryCommand.eventId,
//                                inventory.getClass().getName(),
//                                product.getProductsId(),
//                                productEvent.getEventType(),
//                                this.objectMapper.writeValueAsString(productEvent),
//                                1,
//                                1,
//                                objectMapper.writeValueAsString(createInventoryCommand)
//
//                        )
//                );
//            });
//            inventory.commit();
//        }
    }


    @Deprecated
    @Override
    public void holdProduct(String productId, int quantity) {
        throw new UnsupportedOperationException();
    }

    @Transactional
    @Override
    public void holdProduct(String orderId, String productId, int quantity) {
        Optional<Product> existProductsOptional = productRepository.findById(productId);
        if (existProductsOptional.isEmpty()) return;
        Product existProduct = existProductsOptional.get();
//        InventoryEntity existInventoryEntity = inventoryRepository.findByProductsId(existProduct.getProductsId()).get();
//        existInventoryEntity.hold(quantity);
//        inventoryRepository.save(existInventoryEntity);
//        InventoryReservedEvent inventoryReservedEvent = new InventoryReservedEvent(
//                orderId,
//                productId,
//                quantity
//        );

        Inventory inventory = loadInventory(productId);
        ReserveInventoryCommand reserveInventoryCommand = new ReserveInventoryCommand(
                orderId,
                productId,
                quantity
        );
        inventory.applyCommand(reserveInventoryCommand);
        inventory.getUnCommitedEvents().forEach(productEvent -> {
            inventoryEventEntityRepository.save(
                    new InventoryEventEntity(
                            orderId,
                            inventory.getClass().getName(),
                            inventory.getProductsId(),
                            productEvent.getEventType(),
                            objectMapper.writeValueAsString(productEvent),
                            inventory.getCurrentSequence(),
                            1,
                            objectMapper.writeValueAsString(reserveInventoryCommand)
                    )
            );

            outboxRepository.save(
                    new Outbox(
                            orderId,
                            productEvent.getClass().getName(),
                            objectMapper.writeValueAsString(productEvent)
                    )
            );
        });


        log.info("Inventory {} is held", inventory);
    }

    @Override
    public SearchProductInfoResponseDTO searchProductInfo(String productId) {
        Optional<Product> existProductsOptional = productRepository.findById(productId);
        if (existProductsOptional.isPresent()) {
            Product existProduct = existProductsOptional.get();
            return new SearchProductInfoResponseDTO(
                    existProduct.getProductsId(),
                    existProduct.getName(),
                    existProduct.getDescription(),
                    existProduct.getPrice());
        }
        return null;
    }

    @Deprecated
    @Override
    public void releaseInventory(String productId, int quantity) {
        throw new UnsupportedOperationException();
    }

    @Transactional
    @Override
    public void releaseInventory(String orderId, String productId, int quantity) {
        Optional<Product> existProductsOptional = productRepository.findById(productId);
        if (existProductsOptional.isEmpty()) return;
        Product existProduct = existProductsOptional.get();
        InventoryEntity existInventoryEntity = inventoryRepository.findByProductsId(existProduct.getProductsId()).get();
        existInventoryEntity.release(quantity);
        inventoryRepository.save(existInventoryEntity);
        InventoryReleasedEvent inventoryReleasedEvent = new InventoryReleasedEvent(
                orderId,
                productId,
                quantity
        );

        outboxRepository.save(
                new Outbox(
                        orderId,
                        inventoryReleasedEvent.getClass().getName(),
                        objectMapper.writeValueAsString(inventoryReleasedEvent)
                )
        );
        log.info("Inventory is released : {}", existInventoryEntity);
    }

    @Override
    public Inventory getInventory(String productId) {
        return loadInventory(productId);
    }

    private Inventory loadInventory(String productId) {
        Inventory inventory = new Inventory();
        List<InventoryEventEntity> eventEntityList = inventoryEventEntityRepository.findByAggregateId(productId);
        List<ProductEvent> eventList = eventEntityList.stream().map(event -> {
            if (event.getEventType().equals(EventInfo.PRODUCTS.INVENTORY_CREATED_EVENT_CLASS.getName())) {
                return objectMapper.readValue(event.getEventPayload(), InventoryCreatedEvent.class);
            } else if (event.getEventType().equals(EventInfo.PRODUCTS.INVENTORY_RESERVED_EVENT_CLASS.getName())) {
                return objectMapper.readValue(event.getEventPayload(), InventoryReservedEvent.class);
            } else if (event.getEventType().equals(EventInfo.PRODUCTS.INVENTORY_RELEASED_EVENT_CLASS.getName())) {
                return objectMapper.readValue(event.getEventPayload(), InventoryReleasedEvent.class);
            } else {
                throw new RuntimeException("Unknown event type: " + event.getEventType());
            }
        }).toList();
        eventList.forEach(inventory::applyEvent);

        return inventory;
    }
}
