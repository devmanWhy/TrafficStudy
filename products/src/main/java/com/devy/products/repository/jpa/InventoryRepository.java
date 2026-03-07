package com.devy.products.repository.jpa;

import com.devy.products.domain.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InventoryRepository extends JpaRepository<Inventory, String> {
    public Optional<Inventory> findByProductsId(String productsId);
}
