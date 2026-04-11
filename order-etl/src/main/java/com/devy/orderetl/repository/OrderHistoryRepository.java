package com.devy.orderetl.repository;

import com.devy.orderetl.domain.OrderHistory;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface OrderHistoryRepository extends MongoRepository<OrderHistory, String> {
}
