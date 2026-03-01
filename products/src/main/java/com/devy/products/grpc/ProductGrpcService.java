package com.devy.products.grpc;

import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.grpc.server.service.GrpcService;

@GrpcService
public class ProductGrpcService extends ProductServiceGrpc.ProductServiceImplBase {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    @Override
    public void hold(HoldProductRequest request, StreamObserver<HoldProductResponse> responseObserver) {
        log.info("\uD83C\uDF81 : Product held successfully for : " + request);
        responseObserver.onNext(
                HoldProductResponse.newBuilder()
                        .setStatus("Held")
                        .build());
        responseObserver.onCompleted();
    }
}
