package com.devy.products.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Product {
    @Id
    private String productsId;
    private String name;
    private String description;
    private int price;

    public Product() {
    }

    public Product(String productsId, String name, String description, int price) {
        this.productsId = productsId;
        this.name = name;
        this.description = description;
        this.price = price;
    }

    public String getProductsId() {
        return productsId;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getPrice() {
        return price;
    }

    @Override
    public String toString() {
        return "Products{" +
                "productsId='" + productsId + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", price=" + price +
                '}';
    }
}
