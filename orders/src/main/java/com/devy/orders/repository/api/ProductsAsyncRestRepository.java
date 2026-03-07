package com.devy.orders.repository.api;

import com.devy.common.ApiInfo;
import com.devy.orders.repository.api.response.SearchProductInfoResponseDTO;
import org.apache.hc.client5.http.async.HttpAsyncClient;
import org.apache.hc.client5.http.async.methods.SimpleHttpRequest;
import org.apache.hc.client5.http.async.methods.SimpleHttpResponse;
import org.apache.hc.client5.http.impl.async.CloseableHttpAsyncClient;
import org.apache.hc.core5.concurrent.FutureCallback;
import org.apache.hc.core5.http.ContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Repository;
import tools.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

@Repository
public class ProductsAsyncRestRepository implements ProductsRepository {

    private Logger log = LoggerFactory.getLogger(this.getClass());
    private final CloseableHttpAsyncClient httpAsyncClient;
    private final ObjectMapper objectMapper;

    public ProductsAsyncRestRepository(
            HttpAsyncClient httpAsyncClient
            , ObjectMapper objectMapper) {
        this.httpAsyncClient = (CloseableHttpAsyncClient) httpAsyncClient;
        this.objectMapper = objectMapper;
    }

    @Override
    public String holdProduct(String productId, int quantity) {
        CompletableFuture<String> future = new CompletableFuture<>();

        SimpleHttpRequest request = SimpleHttpRequest.create(
                HttpMethod.POST.name(),
                ApiInfo.PRODUCTS.BASE_URL + "/products" + "/hold"
        );

        request.addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        request.setBody(objectMapper.writeValueAsString(
                new HashMap<String, Object>() {{
                    put("productId", productId);
                    put("quantity", quantity);
                }}
        ), ContentType.APPLICATION_JSON);

        httpAsyncClient.execute(request, new FutureCallback<SimpleHttpResponse>() {

            @Override
            public void completed(SimpleHttpResponse result) {
                future.completeAsync(() -> {
                   try {
                       Thread.sleep(10_000);
                   } catch (InterruptedException e) {
                       e.printStackTrace();
                   }
                    log.info("{} : completeAsync 수행완료", Thread.currentThread().getName());
                   return result.getBodyText();
                });
                log.info("{} : NIO 역할 수행 완료", Thread.currentThread().getName());
            }

            @Override
            public void failed(Exception ex) {

            }

            @Override
            public void cancelled() {

            }
        });


        String result;
        try {
            log.info("{} : Future get 수행 시 블로킹", Thread.currentThread().getName());
            result = future.get();
            log.info("{} : Future 결과 처리 완료 : {}", Thread.currentThread().getName(), result);

        } catch (Exception ex) {
            throw new RuntimeException("Failed to hold product: " + productId, ex);
        }

        return result;
    }

    @Override
    public CompletableFuture<String> asyncHoldProduct(String productId, int quantity) {
        CompletableFuture<String> future = new CompletableFuture<>();

        SimpleHttpRequest request = SimpleHttpRequest.create(
                HttpMethod.POST.name(),
                ApiInfo.PRODUCTS.BASE_URL + "/products" + "/hold"
        );
        request.addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        request.setBody(objectMapper.writeValueAsString(
                new HashMap<String, Object>() {{
                    put("productId", productId);
                    put("quantity", quantity);
                }}
        ), ContentType.APPLICATION_JSON);

        httpAsyncClient.execute(request, new FutureCallback<SimpleHttpResponse>() {

            @Override
            public void completed(SimpleHttpResponse result) {
                log.info("Holding product: " + result.getBody());
                future.complete(result.getBodyText());
            }

            @Override
            public void failed(Exception ex) {
                log.error("Failed to hold product: " + productId, ex);
                future.completeExceptionally(ex);
            }

            @Override
            public void cancelled() {
                log.info("Holding product cancelled: " + productId);
                future.completeExceptionally(new RuntimeException("Holding product cancelled: " + productId));
            }
        });
        return future;
    }

    @Override
    public SearchProductInfoResponseDTO searchProductInfo(String productId) {
        throw new UnsupportedOperationException();
    }
}
