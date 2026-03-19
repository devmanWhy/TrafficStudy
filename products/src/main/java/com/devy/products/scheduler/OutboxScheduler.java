package com.devy.products.scheduler;

import com.devy.common.event.EventInfo;
import com.devy.products.domain.Outbox;
import com.devy.products.repository.jpa.OutboxRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OutboxScheduler {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    private final OutboxRepository outboxRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    public OutboxScheduler(OutboxRepository outboxRepository, KafkaTemplate<String, String> kafkaTemplate) {
        this.outboxRepository = outboxRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Transactional
    @Scheduled(fixedDelay = 1000)
    public void processOutbox() {
        // outBox 조회
        List<Outbox> notPublishedEvents = outboxRepository.findBySuccess(false);
        // 데이터 순회하면서
        notPublishedEvents.forEach(notPublishedEvent -> {
            // 메시지 발행
            if (isProductEvent(notPublishedEvent.getEventType())) {
                log.info("Processing product event: {}", notPublishedEvent);
                kafkaTemplate.send(EventInfo.PRODUCTS.PRODUCT_EVENT_TOPIC, notPublishedEvent.getEventPayload());
                // 상태 success 로 해주기
                notPublishedEvent.complete();
            }
        });
    }

    private boolean isProductEvent(String eventType) {
        return EventInfo.PRODUCTS.INVENTORY_RELEASED_EVENT_CLASS.getName().equals(eventType)
                || EventInfo.PRODUCTS.INVENTORY_RESERVED_EVENT_CLASS.getName().equals(eventType)
                || EventInfo.PRODUCTS.INVENTORY_CREATED_EVENT_CLASS.getName().equals(eventType)
                ;
    }
}
