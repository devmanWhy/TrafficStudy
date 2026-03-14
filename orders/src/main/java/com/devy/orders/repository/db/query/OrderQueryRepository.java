package com.devy.orders.repository.db.query;

import com.devy.orders.domain.Orders;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderQueryRepository extends JpaRepository<Orders, String> {
}
