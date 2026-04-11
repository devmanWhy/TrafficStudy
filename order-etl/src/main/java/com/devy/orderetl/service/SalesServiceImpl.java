package com.devy.orderetl.service;

import com.devy.common.event.order.OrderConfirmedEvent;
import com.devy.common.event.order.OrderEvent;
import com.devy.common.event.order.OrderPlacedEvent;
import com.devy.orderetl.domain.OrderHistory;
import com.devy.orderetl.domain.Sales;
import com.devy.orderetl.repository.OrderHistoryRepository;
import com.devy.orderetl.repository.SalesRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Service
public class SalesServiceImpl implements SalesService {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    private final SalesRepository salesRepository;
    private final OrderHistoryRepository orderHistoryRepository;

    public SalesServiceImpl(SalesRepository salesRepository, OrderHistoryRepository orderHistoryRepository) {
        this.salesRepository = salesRepository;
        this.orderHistoryRepository = orderHistoryRepository;
    }

    @Override
    public void updateSales(OrderEvent orderEvent) {
        if (orderEvent instanceof OrderPlacedEvent orderPlacedEvent) {
            orderHistoryRepository.save(
                    new OrderHistory(
                            orderPlacedEvent.getOrderId(),
                            orderPlacedEvent.getTotalAmount(),
                            false,
                            orderPlacedEvent.eventAt.toInstant().toEpochMilli()
                    )
            );
        }

        if (orderEvent instanceof OrderConfirmedEvent orderConfirmedEvent) {
            Optional<OrderHistory> orderHistory = orderHistoryRepository.findById(orderConfirmedEvent.getOrderId());
            if (orderHistory.isPresent()) {
                String date = ZonedDateTime.ofInstant(Instant.ofEpochMilli(orderHistory.get().getEventAt()), ZoneId.of("Asia/Seoul"))
                        .format(DateTimeFormatter.ofPattern("yyyyMMdd"));
                Optional<Sales> optionalSales = salesRepository.findByDate(date).stream().findFirst();
                if (optionalSales.isPresent()) {
                    optionalSales.get().updateTotalAmount(orderHistory.get().getTotalAmount());
                    salesRepository.save(optionalSales.get());
                } else {
                    salesRepository.save(new Sales(
                            date,
                            orderHistory.get().getTotalAmount()
                    ));
                }
            }
        }

    }

    @Override
    public Sales getSalesByDate(String date) {
        return salesRepository.findByDate(date).stream().findFirst().orElse(null);
    }

}
