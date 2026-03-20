package com.devy.products.service;

import com.devy.common.event.EventInfo;
import com.devy.common.event.product.InventoryCreatedEvent;
import com.devy.common.event.product.InventoryReleasedEvent;
import com.devy.common.event.product.InventoryReservedEvent;
import com.devy.common.event.product.ProductEvent;
import com.devy.common.event.product.command.CreateInventoryCommand;
import com.devy.common.event.product.command.ReleaseInventoryCommand;
import com.devy.common.event.product.command.ReserveInventoryCommand;
import com.devy.common.event.product.registry.ProductEventRegistry;
import com.devy.products.controller.response.SearchProductInfoResponseDTO;
import com.devy.products.domain.Inventory;
import com.devy.products.domain.InventoryEventEntity;
import com.devy.products.domain.Outbox;
import com.devy.products.domain.Product;
import com.devy.products.repository.jpa.InventoryEventEntityRepository;
import com.devy.products.repository.jpa.OutboxRepository;
import com.devy.products.repository.jpa.ProductInventoryRepository;
import com.devy.products.repository.jpa.ProductRepository;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductInventoryRepository productInventoryRepository;
    private final OutboxRepository outboxRepository;
    private final ObjectMapper objectMapper;
    private final InventoryEventEntityRepository inventoryEventEntityRepository;

    private Logger log = LoggerFactory.getLogger(this.getClass());

    public ProductServiceImpl(ProductRepository productRepository, ProductInventoryRepository productInventoryRepository, OutboxRepository outboxRepository, ObjectMapper objectMapper, InventoryEventEntityRepository inventoryEventEntityRepository) {
        this.productRepository = productRepository;
        this.productInventoryRepository = productInventoryRepository;
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
//
//                outboxRepository.save(
//                        new Outbox(
//                                createInventoryCommand.eventId,
//                                productEvent.getClass().getName(),
//                                objectMapper.writeValueAsString(productEvent)
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
        inventory.getUnCommittedEvents().forEach(productEvent -> {
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
        // Inventory 를 가져온다 => Event Sourcing 으로 되어있고 그래서 적재된 Event 를 Replay 해서 현재 상태 만든다
        Inventory inventory = loadInventory(productId);
        if (inventory.getCurrentSequence() > 0) {
            ReleaseInventoryCommand releaseInventoryCommand = new ReleaseInventoryCommand(
                    orderId,
                    productId,
                    quantity
            );
            inventory.applyCommand(releaseInventoryCommand);
            inventory.getUnCommittedEvents().forEach(productEvent -> {
                // EventSource 를 저장
                inventoryEventEntityRepository.save(
                        new InventoryEventEntity(
                                orderId,
                                inventory.getClass().getName(),
                                productId,
                                productEvent.getEventType(),
                                objectMapper.writeValueAsString(productEvent),
                                inventory.getCurrentSequence(),
                                1,
                                objectMapper.writeValueAsString(releaseInventoryCommand)

                        )
                );
                // Outbox 에 Event 저장
                outboxRepository.save(
                        new Outbox(
                                orderId,
                                productEvent.getClass().getName(),
                                objectMapper.writeValueAsString(productEvent)
                        )
                );
            });
        }
    }

    @Override
    public Inventory getInventory(String productId) {
        return loadInventory(productId);
    }

    private Inventory loadInventory(String productId) {
        Inventory inventory = new Inventory();
        List<InventoryEventEntity> eventEntityList = inventoryEventEntityRepository.findByAggregateIdOrderByEventSequence(productId);
        eventEntityList.forEach(event -> {
            // Product Event 를 가져온다.
            ProductEvent productEvent = objectMapper.readValue(event.getEventPayload(), ProductEventRegistry.getEventClass(event.getEventType()));
            // Product Event 를 Apply 한다
            inventory.applyEvent(productEvent);
        });

        return inventory;
    }
}
