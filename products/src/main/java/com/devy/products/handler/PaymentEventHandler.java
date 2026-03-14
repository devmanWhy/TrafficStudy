//package com.devy.products.handler;
//
//import com.devy.common.event.EventInfo;
//import com.devy.common.event.payment.PaymentFailedEvent;
//import com.devy.common.event.product.InventoryReleasedEvent;
//import com.devy.products.domain.ProcessedEvent;
//import com.devy.products.repository.jpa.ProcessedEventRepository;
//import com.devy.products.repository.message.ProductEventMessageRepository;
//import com.devy.products.service.ProductService;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.kafka.annotation.KafkaListener;
//import org.springframework.kafka.support.Acknowledgment;
//import org.springframework.stereotype.Component;
//import tools.jackson.databind.JsonNode;
//import tools.jackson.databind.ObjectMapper;
//
//import java.util.Optional;
//
//import static com.devy.common.event.EventInfo.PAYMENTS.PAYMENT_SUCCEED_EVENT_CLASS;
//
//@Component
//public class PaymentEventHandler {
//    private Logger log = LoggerFactory.getLogger(this.getClass());
//
//    private final ObjectMapper objectMapper;
//    private final ProductService productService;
//    private final ProcessedEventRepository processedEventRepository;
//    private final ProductEventMessageRepository productEventMessageRepository;
//
//    public PaymentEventHandler(ObjectMapper objectMapper, ProductService productService, ProcessedEventRepository processedEventRepository, ProductEventMessageRepository productEventMessageRepository) {
//        this.objectMapper = objectMapper;
//        this.productService = productService;
//        this.processedEventRepository = processedEventRepository;
//        this.productEventMessageRepository = productEventMessageRepository;
//    }
//
//    @KafkaListener(topics = {EventInfo.PAYMENTS.PAYMENT_EVENT_TOPIC})
//    public void handlePaymentEvent(String message, Acknowledgment acknowledgment) {
//        JsonNode jsonNode = objectMapper.readTree(message);
//        JsonNode eventId = jsonNode.get("eventId");
//        JsonNode eventType = jsonNode.get("eventType");
//        Optional<ProcessedEvent> existEventOptional = processedEventRepository.findById(
//                new ProcessedEvent.ProcessedEventId(
//                        "product-" + eventId.asString(), eventType.asString())
//        );
//        if (existEventOptional.isEmpty()) {
//            if (PAYMENT_SUCCEED_EVENT_CLASS.getName().equals(eventType.asString())) {
//                PaymentFailedEvent paymentFailedEvent = objectMapper.readValue(message, PAYMENT_SUCCEED_EVENT_CLASS);
//
//                productService.releaseInventory(paymentFailedEvent.getProductId(), paymentFailedEvent.getQuantity());
//
//                InventoryReleasedEvent inventoryReleasedEvent = new InventoryReleasedEvent(
//                        paymentFailedEvent.getEventId(),
//                        paymentFailedEvent.getUserId(),
//                        paymentFailedEvent.getProductId(),
//                        paymentFailedEvent.getQuantity(),
//                        paymentFailedEvent.getTotalAmount()
//                );
//                productEventMessageRepository.publish(inventoryReleasedEvent);
//                log.info("PaymentFailedEvent: {}", paymentFailedEvent);
//            }
//
//            processedEventRepository.save(new ProcessedEvent(
//                    "product-" + eventId.asString(),
//                    eventType.asString(),
//                    message
//            ));
//        }
//        acknowledgment.acknowledge();
//    }
//}
