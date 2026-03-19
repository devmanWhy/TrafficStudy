package com.devy.products.repository.jpa;

import com.devy.products.domain.ProductInventory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductInventoryRepository extends JpaRepository<ProductInventory, String> {
    public Optional<ProductInventory> findByProductsId(String productsId);
}
