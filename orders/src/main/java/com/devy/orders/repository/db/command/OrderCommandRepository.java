package com.devy.orders.repository.db.command;

import com.devy.orders.domain.Orders;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderCommandRepository extends JpaRepository<Orders, String> {
}
