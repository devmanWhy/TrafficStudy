package com.devy.orders.repository.api.config;

import com.devy.common.ApiInfo;
import com.devy.products.grpc.ProductServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GrpcClientConfig {

    @Bean
    public ProductServiceGrpc.ProductServiceBlockingStub productServiceBlockingStub() {
        ManagedChannel channel = ManagedChannelBuilder
                .forAddress(ApiInfo.PRODUCTS.HOST, Integer.parseInt(ApiInfo.PRODUCTS.PORT))
                .usePlaintext()
                .build();
        return ProductServiceGrpc.newBlockingStub(channel);
    }
}
