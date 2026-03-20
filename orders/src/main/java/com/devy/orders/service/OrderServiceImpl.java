package com.devy.orders.service;

import com.devy.common.event.order.OrderEvent;
import com.devy.common.event.order.OrderPlacedEvent;
import com.devy.common.event.order.command.CancelOrderCommand;
import com.devy.common.event.order.command.ConfirmOrderCommand;
import com.devy.common.event.order.registry.OrderEventRegistry;
import com.devy.orders.controller.request.PlaceOrderRequestDTO;
import com.devy.orders.controller.request.SearchOrderInfoRequestDTO;
import com.devy.orders.controller.response.SearchOrderInfoResponseDTO;
import com.devy.orders.domain.OrderEventEntity;
import com.devy.orders.domain.Orders;
import com.devy.orders.domain.Outbox;
import com.devy.orders.repository.api.PaymentsRepository;
import com.devy.orders.repository.api.ProductsRepository;
import com.devy.orders.repository.api.response.SearchPaymentInfoResponseDTO;
import com.devy.orders.repository.api.response.SearchProductInfoResponseDTO;
import com.devy.orders.repository.db.command.OrderEventEntityCommandRepository;
import com.devy.orders.repository.db.command.OutboxCommandRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.UUID;

@Service
public class OrderServiceImpl implements OrderService {

    private final ProductsRepository productsRepository;
    private final PaymentsRepository paymentsRepository;
    private final OutboxCommandRepository outboxRepository;
    private final OrderEventEntityCommandRepository orderEventEntityCommandRepository;
    private final ObjectMapper objectMapper;

    public OrderServiceImpl(
            @Qualifier("productsRestRepository") ProductsRepository productsRepository, PaymentsRepository paymentsRepository, OutboxCommandRepository outboxRepository, OrderEventEntityCommandRepository orderEventEntityCommandRepository, ObjectMapper objectMapper) {
        this.orderEventEntityCommandRepository = orderEventEntityCommandRepository;
        this.productsRepository = productsRepository;
        this.paymentsRepository = paymentsRepository;
        this.outboxRepository = outboxRepository;
        this.objectMapper = objectMapper;
    }


    @Transactional
    @Override
    public String placeOrder(PlaceOrderRequestDTO request) {
        String orderId = UUID.randomUUID().toString().replace("-", "");
        Orders orders = loadOrder(orderId);
        OrderPlacedEvent orderPlacedEvent = new OrderPlacedEvent(
                orderId,
                request.userId(),
                request.productId(),
                request.quantity(),
                request.totalAmount()
        );
        orders.applyEvent(orderPlacedEvent);
            orderEventEntityCommandRepository.save(
                    new OrderEventEntity(
                            orders.getOrderId(),
                            orders.getClass().getName(),
                            orders.getOrderId(),
                            orderPlacedEvent.eventType,
                            objectMapper.writeValueAsString(orderPlacedEvent),
                            orders.getCurrentSequence(),
                            1,
                            ""
                    )
            );

            outboxRepository.save(
                    new Outbox(
                            orderId,
                            orderPlacedEvent.eventType,
                            objectMapper.writeValueAsString(orderPlacedEvent)
                    )
            );

        return orderId;
    }

    @Override
    public SearchOrderInfoResponseDTO searchOrder(SearchOrderInfoRequestDTO request) {
        Orders orders = loadOrder(request.orderId());
        if (orders.getCurrentSequence() > 0) {
            SearchProductInfoResponseDTO searchProductInfoResponseDTO = productsRepository.searchProductInfo(orders.getProductsId());
            SearchPaymentInfoResponseDTO searchPaymentInfoResponseDTO = paymentsRepository.searchPaymentInfo(orders.getOrderId(), orders.getUserId());
            return new SearchOrderInfoResponseDTO(
                    orders.getOrderId(),
                    orders.getUserId(),
                    orders.getProductsId(),
                    searchProductInfoResponseDTO.name(),
                    searchProductInfoResponseDTO.price(),
                    orders.getQuantity(),
                    searchPaymentInfoResponseDTO.amount(),
                    orders.getOrderStatus(),
                    orders.getOrderAt(),
                    searchPaymentInfoResponseDTO.paidAt()
            );
        }
        return null;
    }

    @Override
    public void cancelOrder(String eventId) {
        Orders orders = loadOrder(eventId);
        if (orders.getCurrentSequence() > 0) {
            CancelOrderCommand cancelOrderCommand = new CancelOrderCommand(orders.getOrderId(), orders.getUserId());
            orders.applyCommand(cancelOrderCommand);
            orders.getUnCommittedEvents().forEach(event -> {

                orderEventEntityCommandRepository.save(
                        new OrderEventEntity(
                                orders.getOrderId(),
                                orders.getClass().getName(),
                                orders.getOrderId(),
                                event.eventType,
                                objectMapper.writeValueAsString(event),
                                orders.getCurrentSequence(),
                                1,
                                objectMapper.writeValueAsString(cancelOrderCommand)
                        )
                );

                outboxRepository.save(
                        new Outbox(
                                event.eventId,
                                event.eventType,
                                objectMapper.writeValueAsString(event)
                        )
                );
            });
        }
    }

    @Override
    public void confirmOrder(String eventId) {
        Orders orders = loadOrder(eventId);
        if (orders.getCurrentSequence() > 0) {
            ConfirmOrderCommand confirmOrderCommand = new ConfirmOrderCommand(orders.getOrderId(), orders.getUserId());
            orders.applyCommand(confirmOrderCommand);
            orders.getUnCommittedEvents().forEach(event -> {

                orderEventEntityCommandRepository.save(
                        new OrderEventEntity(
                                orders.getOrderId(),
                                orders.getClass().getName(),
                                orders.getOrderId(),
                                event.eventType,
                                objectMapper.writeValueAsString(event),
                                orders.getCurrentSequence(),
                                1,
                                objectMapper.writeValueAsString(confirmOrderCommand)
                        )
                );

                outboxRepository.save(
                        new Outbox(
                                event.eventId,
                                event.eventType,
                                objectMapper.writeValueAsString(event)
                        )
                );
            });
        }
    }

    private Orders loadOrder(String orderId) {
        Orders order = new Orders();
        List<OrderEventEntity> orderEvents = orderEventEntityCommandRepository.findByAggregateIdOrderByEventSequence(orderId);
        orderEvents.forEach(event -> {
            OrderEvent orderEvent = objectMapper.readValue(event.getEventPayload(), OrderEventRegistry.getEventClass(event.getEventType()));
            order.applyEvent(orderEvent);
        });
        return order;
    }

}
