package com.devy.payments.repository.jpa;

import com.devy.payments.domain.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, String> {
    public Optional<Payment> findByOrderIdAndUserId(String orderId, String userId);
}
