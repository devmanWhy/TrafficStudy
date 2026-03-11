package com.devy.products.service;

import com.devy.products.controller.response.SearchProductInfoResponseDTO;
import com.devy.products.domain.Inventory;
import com.devy.products.domain.Product;
import com.devy.products.repository.jpa.InventoryRepository;
import com.devy.products.repository.jpa.ProductRepository;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final InventoryRepository inventoryRepository;

    private Logger log = LoggerFactory.getLogger(this.getClass());

    public ProductServiceImpl(ProductRepository productRepository, InventoryRepository inventoryRepository) {
        this.productRepository = productRepository;
        this.inventoryRepository = inventoryRepository;
    }

    @PostConstruct
    public void init() {
//        for (int index = 0; index < 10; index++) {
//            Product product = new Product(
//                    "product-" + index,
//                    "상품 " + index,
//                    "정말 좋은 상품.",
//                    10000 + index
//            );
//            Inventory inventory = new Inventory(
//                    "inventory-" + index,
//                    product.getProductsId(),
//                    100
//            );
//            productRepository.save(product);
//            inventoryRepository.save(inventory);
//        }
    }


    @Override
    public void holdProduct(String productId, int quantity) {
        Optional<Product> existProductsOptional = productRepository.findById(productId);
        if (existProductsOptional.isEmpty()) return;
        Product existProduct = existProductsOptional.get();
        Inventory existInventory = inventoryRepository.findByProductsId(existProduct.getProductsId()).get();
        existInventory.hold(quantity);
        inventoryRepository.save(existInventory);
        log.info("Inventory {} is held", existInventory);
    }

    @Override
    public SearchProductInfoResponseDTO searchProductInfo(String productId) {
        Optional<Product> existProductsOptional = productRepository.findById(productId);
        if (existProductsOptional.isPresent()) {
            Product existProduct = existProductsOptional.get();
            return new SearchProductInfoResponseDTO(
                    existProduct.getProductsId(),
                    existProduct.getName(),
                    existProduct.getDescription(),
                    existProduct.getPrice());
        }
        return null;
    }

    @Override
    public void releaseInventory(String productId, int quantity) {
        Optional<Product> existProductsOptional = productRepository.findById(productId);
        if (existProductsOptional.isEmpty()) return;
        Product existProduct = existProductsOptional.get();
        Inventory existInventory = inventoryRepository.findByProductsId(existProduct.getProductsId()).get();
        existInventory.release(quantity);
        inventoryRepository.save(existInventory);
        log.info("Inventory is released : {}", existInventory);
    }
}
