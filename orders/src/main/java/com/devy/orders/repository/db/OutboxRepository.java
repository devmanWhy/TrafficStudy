package com.devy.orders.repository.db;

import com.devy.orders.domain.Outbox;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OutboxRepository extends JpaRepository<Outbox, String> {
    public List<Outbox> findBySuccess(boolean success);
}
