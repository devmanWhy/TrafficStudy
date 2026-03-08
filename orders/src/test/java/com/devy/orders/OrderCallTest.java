package com.devy.orders;

import com.devy.common.ApiInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatusCode;
import org.springframework.test.web.servlet.client.ExchangeResult;
import org.springframework.test.web.servlet.client.RestTestClient;

import java.time.ZonedDateTime;
import java.util.HashMap;

@SpringBootTest(classes = {RestTestClient.class})
public class OrderCallTest {

    private RestTestClient client;

    private Logger log = LoggerFactory.getLogger(OrderCallTest.class);

    @BeforeEach
    public void setUp() {
        client = RestTestClient.bindToServer().baseUrl("http://localhost:8080")
                .build();
    }

    @DisplayName("지속적인 주문 조회 트래픽")
    @Test
    public void testSearchOrderWithContinuousTraffic() {
        ZonedDateTime start = ZonedDateTime.now();
        int allowedCount = 0;
        while (start.plusSeconds(60).isAfter(ZonedDateTime.now())) {
            RestTestClient.ResponseSpec responseSpec = client.post().uri(ApiInfo.ORDERS.BASE_PATH + "/orders/search")
                    .body(new HashMap<String, Object>() {{
                        put("userId", "user-001");
                        put("orderId", "639c4981b5b842c29726fd8d766e2d55");
                    }})
                    .exchange();
            ExchangeResult exchangeResult = responseSpec.returnResult();
            HttpStatusCode statusCode = exchangeResult.getStatus();
            if (statusCode.is2xxSuccessful()) {
                log.info("주문 조회 성공! : {}", new String(exchangeResult.getResponseBodyContent()));
                allowedCount++;
            } else {
                log.info("주문 조회 실패! : {}", new String(exchangeResult.getResponseBodyContent()));
            }
        }
        log.info("Allowed Count : {}", allowedCount);
    }

    @DisplayName("지속적인 주문 트래픽")
    @Test
    public void testPlaceOrderWithContinuousTraffic() {
        ZonedDateTime start = ZonedDateTime.now();
        int allowedCount = 0;
        while (start.plusSeconds(1).isAfter(ZonedDateTime.now())) {
            RestTestClient.ResponseSpec responseSpec = client.post().uri(ApiInfo.ORDERS.BASE_PATH + "/orders/place")
                    .body(new HashMap<String, Object>() {{
                        put("userId", "user-001");
                        put("productId", "product-8");
                        put("quantity", 5);
                        put("totalAmount", 6000);
                    }})
                    .exchange();
            ExchangeResult exchangeResult = responseSpec.returnResult();
            HttpStatusCode statusCode = exchangeResult.getStatus();
            if (statusCode.is2xxSuccessful()) {
                log.info("주문 성공! : {}", new String(exchangeResult.getResponseBodyContent()));
                allowedCount++;
            } else {
                log.info("주문 실패! : {}", new String(exchangeResult.getResponseBodyContent()));
            }
        }
        log.info("Allowed Count : {}", allowedCount);
    }
}
