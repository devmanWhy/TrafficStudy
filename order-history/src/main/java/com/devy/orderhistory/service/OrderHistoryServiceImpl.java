package com.devy.orderhistory.service;

import com.devy.common.event.EventInfo;
import com.devy.orderhistory.domain.OrderHistory;
import com.devy.orderhistory.repository.OrderHistoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class OrderHistoryServiceImpl implements OrderHistoryService {

    private Logger log = LoggerFactory.getLogger(this.getClass());
    private final OrderHistoryRepository orderHistoryRepository;
    private final ObjectMapper objectMapper;

    public OrderHistoryServiceImpl(OrderHistoryRepository orderHistoryRepository, ObjectMapper objectMapper) {
        this.orderHistoryRepository = orderHistoryRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    public void saveOrderHistory(String orderId, String eventType, String message) {
        List<OrderHistory> orderHistories = orderHistoryRepository.findByOrderId(orderId);
        Optional<OrderHistory> existsHistory = orderHistories.stream()
                .filter(orderHistory -> orderHistory.getEventType().equals(eventType))
                .findFirst();
        if (existsHistory.isEmpty()) {
            log.info("Saving new order history for order: {} with event type: {} and message: {}", orderId, eventType, message);
            orderHistoryRepository.save(
                    new OrderHistory(
                            UUID.randomUUID().toString(),
                            orderId,
                            eventType,
                            message
                    )
            );
        }
    }

    @Override
    public List<Object> getOrderHistory(String orderId) {
        return orderHistoryRepository.findByOrderId(orderId)
                .stream().map(orderHistory -> objectMapper.readValue(
                        orderHistory.getEventPayload(),
                        findEventClass(orderHistory.getEventType())
                )).toList();
    }

    private Class findEventClass(String eventType) {
        if (EventInfo.ORDERS.ORDER_PLACED_EVENT_CLASS.getName().equals(eventType)) {
            return EventInfo.ORDERS.ORDER_PLACED_EVENT_CLASS;
        }
        if (EventInfo.PRODUCTS.INVENTORY_RESERVED_EVENT_CLASS.getName().equals(eventType)) {
            return EventInfo.PRODUCTS.INVENTORY_RESERVED_EVENT_CLASS;
        }

        if (EventInfo.PRODUCTS.INVENTORY_RELEASED_EVENT_CLASS.getName().equals(eventType)) {
            return EventInfo.PRODUCTS.INVENTORY_RELEASED_EVENT_CLASS;
        }

        if (EventInfo.PAYMENTS.PAYMENT_SUCCEED_EVENT_CLASS.getName().equals(eventType)) {
            return EventInfo.PAYMENTS.PAYMENT_SUCCEED_EVENT_CLASS;
        }

        if (EventInfo.PAYMENTS.PAYMENT_FAILED_EVENT_CLASS.getName().equals(eventType)) {
            return EventInfo.PAYMENTS.PAYMENT_FAILED_EVENT_CLASS;
        }
        return null;
    }
}
