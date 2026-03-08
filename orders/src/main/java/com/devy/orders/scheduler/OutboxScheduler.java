package com.devy.orders.scheduler;

import com.devy.common.event.EventInfo;
import com.devy.common.event.order.OrderPlacedEvent;
import com.devy.orders.domain.Outbox;
import com.devy.orders.repository.db.OutboxRepository;
import com.devy.orders.repository.message.OrderEventMessageRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

@Component
public class OutboxScheduler {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    private final OutboxRepository outboxRepository;
    private final OrderEventMessageRepository orderEventMessageRepository;
    private final ObjectMapper objectMapper;

    public OutboxScheduler(OutboxRepository outboxRepository, OrderEventMessageRepository orderEventMessageRepository, ObjectMapper objectMapper) {
        this.outboxRepository = outboxRepository;
        this.orderEventMessageRepository = orderEventMessageRepository;
        this.objectMapper = objectMapper;
    }

    @Transactional
    @Scheduled(fixedDelay = 1000)
    public void processOutbox() {
        // outBox 조회
        List<Outbox> notPublishedEvents = outboxRepository.findBySuccess(false);
        // 데이터 순회하면서
        notPublishedEvents.forEach(notPublishedEvent -> {
            log.info("Processing event: {}", notPublishedEvent);
            // 메시지 발행
            if (EventInfo.ORDERS.ORDER_PLACED_EVENT_CLASS.getName().equals(notPublishedEvent.getEventType())) {
                OrderPlacedEvent orderPlacedEvent = objectMapper.readValue(notPublishedEvent.getEventPayload(), EventInfo.ORDERS.ORDER_PLACED_EVENT_CLASS);
                orderEventMessageRepository.publish(
                        orderPlacedEvent
                );
                // 상태 success 로 해주기
                notPublishedEvent.complete();
            }
        });
    }
}
