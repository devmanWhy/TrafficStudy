package com.devy.products.service;

import com.devy.common.event.product.ProductEvent;
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
import com.devy.products.repository.redis.CacheRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.RedisSystemException;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Optional;

import static com.devy.products.repository.redis.RedisRepository.PRODUCT_PREFIX;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductInventoryRepository productInventoryRepository;
    private final OutboxRepository outboxRepository;
    private final ObjectMapper objectMapper;
    private final InventoryEventEntityRepository inventoryEventEntityRepository;
    private final CacheRepository cacheRepository;

    private Logger log = LoggerFactory.getLogger(this.getClass());

    public ProductServiceImpl(ProductRepository productRepository, ProductInventoryRepository productInventoryRepository, OutboxRepository outboxRepository, ObjectMapper objectMapper, InventoryEventEntityRepository inventoryEventEntityRepository, CacheRepository cacheRepository) {
        this.productRepository = productRepository;
        this.productInventoryRepository = productInventoryRepository;
        this.outboxRepository = outboxRepository;
        this.objectMapper = objectMapper;
        this.inventoryEventEntityRepository = inventoryEventEntityRepository;
        this.cacheRepository = cacheRepository;
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

    @CircuitBreaker(name = "getInventory", fallbackMethod = "fallbackGetInventory")
    @Override
    public Inventory getInventory(String productId) {
        Inventory existInventory = findInventoryCache(productId);
        if (existInventory != null) return existInventory;
        if (cacheRepository.getLock(productId)) {
            try {
                log.info("getInventory 락 획득 성공! : {}", productId);
                Inventory secondCache = findInventoryCache(productId);
                if (secondCache != null) {
                    log.info("secondCache 에서 데이터 찾음!");
                    return secondCache;
                }
                return loadInventory(productId);
            } catch (Exception e) {
                log.error("락 획득 후 로직 실행 실패 : {}", e.getMessage());
            } finally {
                cacheRepository.releaseLock(productId);
            }

        } else {
            log.info("getInventory 락 획득 실패!");
            try {
                Thread.sleep(100 + (int) (Math.random() * 50));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            return getInventory(productId);
        }
        return null;
    }

    private Inventory fallbackGetInventory(String productId, Throwable throwable) {
        if (throwable instanceof RedisConnectionFailureException
                || throwable instanceof RedisSystemException) {
            log.info("Redis 조회 비정상 : DB 조회로 변경");
            return loadInventory(productId);
        }
        return null;
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

        try {
            cacheRepository.save(PRODUCT_PREFIX + productId, inventory);
        } catch (Exception e) {
            log.warn("Redis 저장 이슈 : {}", e.getMessage());
        }
        return inventory;
    }

    private Inventory findInventoryCache(String productId) {
        Inventory existInventory = cacheRepository.getValue(PRODUCT_PREFIX + productId, Inventory.class);
        if (existInventory != null) {
            log.info("Inventory Cache {} is found", existInventory);
            return existInventory;
        }
        return null;
    }

    private void saveInventoryCache(Inventory inventory) {

    }
}
