package com.devy.orderetl.repository;

import com.devy.orderetl.domain.Sales;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface SalesRepository extends MongoRepository<Sales, String> {
    public List<Sales> findByDate(String date);
}
