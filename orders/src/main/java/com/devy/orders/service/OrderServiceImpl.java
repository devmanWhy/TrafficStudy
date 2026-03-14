package com.devy.orders.service;

import com.devy.common.event.order.OrderPlacedEvent;
import com.devy.orders.controller.request.PlaceOrderRequestDTO;
import com.devy.orders.controller.request.SearchOrderInfoRequestDTO;
import com.devy.orders.controller.response.SearchOrderInfoResponseDTO;
import com.devy.orders.domain.Orders;
import com.devy.orders.domain.Outbox;
import com.devy.orders.repository.api.PaymentsRepository;
import com.devy.orders.repository.api.ProductsRepository;
import com.devy.orders.repository.api.response.SearchPaymentInfoResponseDTO;
import com.devy.orders.repository.api.response.SearchProductInfoResponseDTO;
import com.devy.orders.repository.db.command.OrderCommandRepository;
import com.devy.orders.repository.db.command.OutboxCommandRepository;
import com.devy.orders.repository.db.query.OrderQueryRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.util.Optional;
import java.util.UUID;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderCommandRepository orderCommandRepository;
    private final OrderQueryRepository orderQueryRepository;
    private final ProductsRepository productsRepository;
    private final PaymentsRepository paymentsRepository;
    private final OutboxCommandRepository outboxRepository;
    private final ObjectMapper objectMapper;

    public OrderServiceImpl(OrderCommandRepository orderCommandRepository, OrderQueryRepository orderQueryRepository,
                            @Qualifier("productsRestRepository") ProductsRepository productsRepository, PaymentsRepository paymentsRepository, OutboxCommandRepository outboxRepository, ObjectMapper objectMapper) {
        this.orderCommandRepository = orderCommandRepository;
        this.orderQueryRepository = orderQueryRepository;
        this.productsRepository = productsRepository;
        this.paymentsRepository = paymentsRepository;
        this.outboxRepository = outboxRepository;
        this.objectMapper = objectMapper;
    }


    @Transactional
    @Override
    public String placeOrder(PlaceOrderRequestDTO request) {
        String orderId = UUID.randomUUID().toString().replace("-", "");
        orderCommandRepository.save(new Orders(orderId, request.userId(), request.productId(), request.quantity(), request.totalAmount()));
        OrderPlacedEvent orderPlacedEvent = new OrderPlacedEvent(
                orderId,
                request.userId(),
                request.productId(),
                request.quantity(),
                request.totalAmount()
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
        Optional<Orders> existsOrderOptional = orderQueryRepository.findById(request.orderId());
        if (existsOrderOptional.isEmpty()) {
            return null;
        }
        Orders existsOrders = existsOrderOptional.get();
        SearchProductInfoResponseDTO searchProductInfoResponseDTO = productsRepository.searchProductInfo(existsOrders.getProductsId());
        SearchPaymentInfoResponseDTO searchPaymentInfoResponseDTO = paymentsRepository.searchPaymentInfo(existsOrders.getOrderId(), existsOrders.getUserId());
        return new SearchOrderInfoResponseDTO(
                existsOrders.getOrderId(),
                existsOrders.getUserId(),
                existsOrders.getProductsId(),
                searchProductInfoResponseDTO.name(),
                searchProductInfoResponseDTO.price(),
                existsOrders.getQuantity(),
                searchPaymentInfoResponseDTO.amount(),
                existsOrders.getOrderStatus(),
                existsOrders.getOrderAt(),
                searchPaymentInfoResponseDTO.paidAt()
        );
    }

    @Override
    public void cancelOrder(String eventId) {
        Optional<Orders> existsOrderOptional = orderCommandRepository.findById(eventId);
        if (existsOrderOptional.isEmpty()) {
            return;
        }
        Orders existsOrders = existsOrderOptional.get();
        existsOrders.cancel();
        orderCommandRepository.save(existsOrders);
    }

    @Override
    public void confirmOrder(String eventId) {
        Optional<Orders> existsOrderOptional = orderCommandRepository.findById(eventId);
        if (existsOrderOptional.isEmpty()) {
            return;
        }
        Orders existsOrders = existsOrderOptional.get();
        existsOrders.confirm();
        orderCommandRepository.save(existsOrders);
    }

}
