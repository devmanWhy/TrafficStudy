package com.devy.orders.repository.api;

import com.devy.orders.repository.api.response.DoPaymentResponseDTO;
import com.devy.orders.repository.api.response.SearchPaymentInfoResponseDTO;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestClient;

import java.util.HashMap;

@Repository
public class PaymentsRestRepository implements PaymentsRepository {

    private final RestClient paymentsRestClient;
    private Logger log = LoggerFactory.getLogger(this.getClass());

    public PaymentsRestRepository(RestClient paymentsRestClient) {
        this.paymentsRestClient = paymentsRestClient;
    }

    @CircuitBreaker(name = "payments-service", fallbackMethod = "fallbackSearchPaymentInfo")
    @Override
    public SearchPaymentInfoResponseDTO searchPaymentInfo(String orderId, String userId) {
        RestClient.ResponseSpec retrieve = paymentsRestClient.post().uri("/payments/search").body(new HashMap<String, Object>() {{
            put("orderId", orderId);
            put("userId", userId);
        }}).retrieve();
        return retrieve.body(SearchPaymentInfoResponseDTO.class);
    }

    public SearchPaymentInfoResponseDTO fallbackSearchPaymentInfo(String orderId, String userId, Throwable throwable) {
        log.warn("Warn in searching payment info for order: {} and user: {}", orderId, userId, throwable);

        return new SearchPaymentInfoResponseDTO(
                orderId,
                0,
                null
        );
    }

    @Override
    public boolean pay(String orderId, String userId, long amount) {
        RestClient.ResponseSpec retrieve = paymentsRestClient.post().uri("/payments/pay").body(new HashMap<String, Object>() {{
            put("orderId", orderId);
            put("userId", userId);
            put("amount", amount);
        }}).retrieve();
        DoPaymentResponseDTO body = retrieve.body(DoPaymentResponseDTO.class);
        log.info("Received From Payments Service : " + body);
        return body.success();
    }
}
