package com.devy.orderetl.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.ZonedDateTime;
import java.util.UUID;

@Document(collection = "sales")
public class Sales {
    @Id
    private String salesId;

    @Indexed
    private String date;
    private Long totalAmount;
    private Long createdAt;
    private Long updatedAt;

    public Sales() {
    }

    public Sales(String date, Long totalAmount) {
        this(UUID.randomUUID().toString(), date, totalAmount, ZonedDateTime.now().toInstant().toEpochMilli(), ZonedDateTime.now().toInstant().toEpochMilli());
    }

    public Sales(String salesId, String date, Long totalAmount, Long createdAt, Long updatedAt) {
        this.salesId = salesId;
        this.date = date;
        this.totalAmount = totalAmount;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public void updateTotalAmount(Long totalAmount) {
        this.totalAmount += totalAmount;
        this.updatedAt = ZonedDateTime.now().toInstant().toEpochMilli();
    }

    public String getSalesId() {
        return salesId;
    }

    public String getDate() {
        return date;
    }

    public Long getTotalAmount() {
        return totalAmount;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public Long getUpdatedAt() {
        return updatedAt;
    }
}
