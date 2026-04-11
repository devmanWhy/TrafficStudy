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
            log.info("주문 접수 됨 : {}", orderPlacedEvent);
            orderHistoryRepository.save(
                    new OrderHistory(
                            orderPlacedEvent.getOrderId(),
                            orderPlacedEvent.getTotalAmount(),
                            false
                    )
            );
        }

        if (orderEvent instanceof OrderConfirmedEvent orderConfirmedEvent) {
            // orderHistory 조회
            Optional<OrderHistory> orderHistory = orderHistoryRepository.findById(orderConfirmedEvent.getOrderId());
            if (orderHistory.isPresent()) {
                ZonedDateTime eventZonedDateTime = ZonedDateTime.ofInstant(Instant.ofEpochMilli(orderHistory.get().getEventAt()), ZoneId.of("Asia/Seoul"));
                String date = eventZonedDateTime.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
                // 오늘 날짜의 Sales 데이터가 있는지
                Optional<Sales> optionalSales = salesRepository.findByDate(date).stream().findFirst();
                // 있으면 Update 없으면 INSERT (UPSERT)
                if (optionalSales.isPresent()) {
                    log.info("오늘 매출액 데이터 존재 : {}, {}", optionalSales.get(), orderConfirmedEvent);
                    optionalSales.get().updateTotalAmount(orderHistory.get().getTotalAmount());
                    salesRepository.save(optionalSales.get());
                } else {
                    log.info("오늘 매출액 데이터 미존재, 신규 생성 : {}", orderConfirmedEvent);
                    salesRepository.save(
                            new Sales(
                                    date,
                                    orderHistory.get().getTotalAmount()
                            )
                    );
                }
            }
        }


    }

    @Override
    public Sales getSalesByDate(String date) {
        return salesRepository.findByDate(date).stream().findFirst().orElse(null);
    }
}
