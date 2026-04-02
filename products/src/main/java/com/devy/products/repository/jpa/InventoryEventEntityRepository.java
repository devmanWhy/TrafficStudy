package com.devy.products.repository.jpa;

import com.devy.products.domain.InventoryEventEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InventoryEventEntityRepository extends JpaRepository<InventoryEventEntity, InventoryEventEntity.InventoryEventEntityId> {
    public List<InventoryEventEntity> findByAggregateIdOrderByEventSequence(String aggregateId);
    public List<InventoryEventEntity> findByAggregateIdAndEventSequenceGreaterThanOrderByEventSequence(String aggregateId, int eventSequence);
}
