package com.devy.orderhistory.repository;

import com.devy.orderhistory.domain.OrderHistory;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface OrderHistoryRepository extends MongoRepository<OrderHistory, String> {

    public List<OrderHistory> findByOrderId(String orderId);
}
