package com.devy.orders.service;

import com.devy.common.event.order.OrderPlacedEvent;
import com.devy.orders.controller.request.PlaceOrderRequestDTO;
import com.devy.orders.controller.request.SearchOrderInfoRequestDTO;
import com.devy.orders.controller.response.SearchOrderInfoResponseDTO;
import com.devy.orders.domain.Orders;
import com.devy.orders.repository.api.PaymentsRepository;
import com.devy.orders.repository.api.ProductsRepository;
import com.devy.orders.repository.api.response.SearchPaymentInfoResponseDTO;
import com.devy.orders.repository.api.response.SearchProductInfoResponseDTO;
import com.devy.orders.repository.db.OrderRepository;
import com.devy.orders.repository.message.OrderEventMessageRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ProductsRepository productsRepository;
    private final PaymentsRepository paymentsRepository;
    private final OrderEventMessageRepository orderEventMessageRepository;

    public OrderServiceImpl(
            OrderRepository orderRepository,
            @Qualifier("productsRestRepository") ProductsRepository productsRepository,
            PaymentsRepository paymentsRepository, OrderEventMessageRepository orderEventMessageRepository
    ) {
        this.orderRepository = orderRepository;
        this.productsRepository = productsRepository;
        this.paymentsRepository = paymentsRepository;
        this.orderEventMessageRepository = orderEventMessageRepository;
    }

    @Override
    public String placeOrder(PlaceOrderRequestDTO request) {
        String orderId = UUID.randomUUID().toString().replace("-", "");
        orderRepository.save(new Orders(orderId, request.userId(), request.productId(), request.quantity(), request.totalAmount()));
        orderEventMessageRepository.publish(
                new OrderPlacedEvent(
                        orderId,
                        request.userId(),
                        request.productId(),
                        request.quantity(),
                        request.totalAmount()
                )
        );
        return orderId;
    }

    @Override
    public SearchOrderInfoResponseDTO searchOrder(SearchOrderInfoRequestDTO request) {
        Optional<Orders> existsOrderOptional = orderRepository.findById(request.orderId());
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
                existsOrders.getOrderAt(),
                searchPaymentInfoResponseDTO.paidAt()
        );
    }
}
