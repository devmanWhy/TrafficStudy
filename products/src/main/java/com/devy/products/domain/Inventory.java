package com.devy.products.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Inventory {

    @Id
    private String inventoryId;
    private String productsId;
    private int quantity;

    public Inventory() {
    }

    public Inventory(String inventoryId, String productsId, int quantity) {
        this.inventoryId = inventoryId;
        this.productsId = productsId;
        this.quantity = quantity;
    }

    private boolean canHold(int quantity) {
        return this.quantity >= quantity;
    }

    public boolean hold(int quantity) {
        if (canHold(quantity)) {
            this.quantity -= quantity;
            return true;
        }
        return false;
    }

    public String getInventoryId() {
        return inventoryId;
    }

    public String getProductsId() {
        return productsId;
    }

    public int getQuantity() {
        return quantity;
    }

    @Override
    public String toString() {
        return "Inventory{" +
                "inventoryId='" + inventoryId + '\'' +
                ", productsId='" + productsId + '\'' +
                ", quantity=" + quantity +
                '}';
    }
}
