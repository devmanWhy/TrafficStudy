package com.devy.orders.repository.api;

import com.devy.products.grpc.HoldProductRequest;
import com.devy.products.grpc.HoldProductResponse;
import com.devy.products.grpc.ProductServiceGrpc;
import org.springframework.stereotype.Repository;

@Repository
public class ProductsGrpcRepository implements ProductsRepository {


    private final ProductServiceGrpc.ProductServiceBlockingStub productServiceBlockingStub;

    public ProductsGrpcRepository(ProductServiceGrpc.ProductServiceBlockingStub productServiceBlockingStub) {
        this.productServiceBlockingStub = productServiceBlockingStub;
    }

    @Override
    public String holdProduct(String productId, int quantity) {
        HoldProductResponse hold = productServiceBlockingStub.hold(HoldProductRequest.newBuilder()
                .setProductId(productId)
                .setQuantity(quantity)
                .build());
        return hold.getStatus();
    }
}
