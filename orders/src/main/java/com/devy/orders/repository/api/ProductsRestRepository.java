package com.devy.orders.repository.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestClient;

import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

@Repository
public class ProductsRestRepository implements ProductsRepository {

    private final RestClient productsRestClient;
    private Logger log = LoggerFactory.getLogger(this.getClass());

    public ProductsRestRepository(RestClient productsRestClient) {
        this.productsRestClient = productsRestClient;
    }

    @Override
    public String holdProduct(String productId, int quantity) {
        RestClient.ResponseSpec retrieve = productsRestClient.post().uri("/products/hold").contentType(MediaType.APPLICATION_JSON).body(
                new HashMap<String, Object>() {{
                    put("productId", productId);
                    put("quantity", quantity);
                }}
        ).retrieve();
        log.info("\uD83D\uDCDE : Received From Product Service : " + retrieve.body(String.class));
        return "OK";
    }

    @Override
    public CompletableFuture<String> asyncHoldProduct(String productId, int quantity) {
        throw new UnsupportedOperationException();
    }
}
