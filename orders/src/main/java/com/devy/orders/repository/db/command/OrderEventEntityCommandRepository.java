package com.devy.orders.repository.db.command;

import com.devy.orders.domain.OrderEventEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderEventEntityCommandRepository extends JpaRepository<OrderEventEntity, OrderEventEntity.OrderEventEntityId> {
    public List<OrderEventEntity> findByAggregateIdOrderByEventSequence(String aggregateId);
}
