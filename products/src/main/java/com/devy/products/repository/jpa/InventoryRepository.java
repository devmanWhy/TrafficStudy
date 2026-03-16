package com.devy.products.repository.jpa;

import com.devy.products.domain.InventoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InventoryRepository extends JpaRepository<InventoryEntity, String> {
    public Optional<InventoryEntity> findByProductsId(String productsId);
}
