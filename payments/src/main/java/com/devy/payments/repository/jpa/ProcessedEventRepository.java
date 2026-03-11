package com.devy.payments.repository.jpa;

import com.devy.payments.domain.ProcessedEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProcessedEventRepository extends JpaRepository<ProcessedEvent, ProcessedEvent.ProcessedEventId> {
}
