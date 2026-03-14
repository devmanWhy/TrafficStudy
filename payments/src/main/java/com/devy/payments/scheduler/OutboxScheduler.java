package com.devy.payments.scheduler;

import com.devy.common.event.EventInfo;
import com.devy.payments.domain.Outbox;
import com.devy.payments.repository.jpa.OutboxRepository;
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
            if (isPaymentEvent(notPublishedEvent.getEventType())) {
                log.info("Processing payment event: {}", notPublishedEvent);
                kafkaTemplate.send(EventInfo.PAYMENTS.PAYMENT_EVENT_TOPIC, notPublishedEvent.getEventPayload());
                // 상태 success 로 해주기
                notPublishedEvent.complete();
            }
        });
    }

    private boolean isPaymentEvent(String eventType) {
        return EventInfo.PAYMENTS.PAYMENT_SUCCEED_EVENT_CLASS.getName().equals(eventType)
                || EventInfo.PAYMENTS.PAYMENT_FAILED_EVENT_CLASS.getName().equals(eventType)
                ;
    }
}
