package com.devy.payments.repository.jpa;

import com.devy.payments.domain.Outbox;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OutboxRepository extends JpaRepository<Outbox, String> {
    public List<Outbox> findBySuccess(boolean success);
}
