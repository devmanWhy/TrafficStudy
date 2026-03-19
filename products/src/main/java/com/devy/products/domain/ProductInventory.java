package com.devy.products.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class ProductInventory {

    @Id
    private String productsId;
    private String productName;
    private String productDescription;
    private int quantity;

    public ProductInventory() {
    }

    public ProductInventory(String productsId, String productName, String productDescription, int quantity) {
        this.productsId = productsId;
        this.productName = productName;
        this.productDescription = productDescription;
        this.quantity = quantity;
    }


    public String getProductsId() {
        return productsId;
    }

    public String getProductName() {
        return productName;
    }

    public String getProductDescription() {
        return productDescription;
    }

    public int getQuantity() {
        return quantity;
    }

    @Override
    public String toString() {
        return "InventoryEntity{" +
                ", productsId='" + productsId + '\'' +
                ", productName='" + productName + '\'' +
                ", productDescription='" + productDescription + '\'' +
                ", quantity=" + quantity +
                '}';
    }
}
