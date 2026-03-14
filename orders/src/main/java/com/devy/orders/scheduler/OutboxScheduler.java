package com.devy.orders.scheduler;

import com.devy.common.event.EventInfo;
import com.devy.orders.domain.Outbox;
import com.devy.orders.repository.db.command.OutboxCommandRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

@Component
public class OutboxScheduler {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    private final OutboxCommandRepository outboxRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public OutboxScheduler(OutboxCommandRepository outboxRepository, KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.outboxRepository = outboxRepository;
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    @Transactional
    @Scheduled(fixedDelay = 1000)
    public void processOutbox() {
        // outBox 조회
        List<Outbox> notPublishedEvents = outboxRepository.findBySuccess(false);
        // 데이터 순회하면서
        notPublishedEvents.forEach(notPublishedEvent -> {
            // 메시지 발행
            if (isOrderEvent(notPublishedEvent.getEventType())) {
                log.info("Processing order event: {}", notPublishedEvent);
                kafkaTemplate.send(EventInfo.ORDERS.ORDER_EVENT_TOPIC, notPublishedEvent.getEventPayload());
                // 상태 success 로 해주기
                notPublishedEvent.complete();
            }
        });
    }

    private boolean isOrderEvent(String eventType) {
        return EventInfo.ORDERS.ORDER_PLACED_EVENT_CLASS.getName().equals(eventType);
    }
}
