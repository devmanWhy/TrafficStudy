package com.devy.products.repository.jpa;

import com.devy.products.domain.EventSnapshotEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventSnapshotEntityRepository extends JpaRepository<EventSnapshotEntity, EventSnapshotEntity.EventSnapshotEntityId> {
}
