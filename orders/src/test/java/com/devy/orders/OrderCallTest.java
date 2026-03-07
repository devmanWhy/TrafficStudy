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

    //--> 요청을 보낼 준비
    private final int CONCURRENCY = 10;

    private RestTestClient client;

    private Logger log = LoggerFactory.getLogger(OrderCallTest.class);

    @BeforeEach
    public void setUp() {
        client = RestTestClient.bindToServer().baseUrl("http://localhost:8080")
                .build();
    }

    @DisplayName("지속적인 주문 조회 트래픽")
    @Test
    public void testSearchOrderWithContinuousTraffic() throws InterruptedException {
        ZonedDateTime start = ZonedDateTime.now();
        int allowedCount = 0;
        while (start.plusSeconds(30).isAfter(ZonedDateTime.now())) {
            RestTestClient.ResponseSpec responseSpec = client.post().uri(ApiInfo.ORDERS.BASE_PATH + "/orders/search")
                    .body(new HashMap<String, Object>() {{
                        put("orderId", "a4cb35d261ca4a93a49c5663e3da58a1");
                        put("userId", "user-001");
                    }})
                    .exchange();
            ExchangeResult exchangeResult = responseSpec.returnResult();
            HttpStatusCode status = exchangeResult.getStatus();
            if (status.is2xxSuccessful()) {
                log.info("주문 조회 성공! : {}", new String(exchangeResult.getResponseBodyContent()));
                allowedCount++;
            }
            Thread.sleep(100);
        }
        log.info("allowed Count : {}", allowedCount);
    }

    @DisplayName("지속적인 주문 트래픽")
    @Test
    public void testPlaceOrderWithContinuousTraffic() throws InterruptedException {
        ZonedDateTime start = ZonedDateTime.now();
        int allowedCount = 0;
        while (start.plusSeconds(30).isAfter(ZonedDateTime.now())) {
            RestTestClient.ResponseSpec responseSpec = client.post().uri(ApiInfo.ORDERS.BASE_PATH + "/orders/place")
                    .body(new HashMap<String, Object>() {{
                        put("userId", "user-01");
                        put("productId", "product-0");
                        put("quantity", 100);
                        put("totalAmount", 10000);
                    }})
                    .exchange();
            ExchangeResult exchangeResult = responseSpec.returnResult();
            HttpStatusCode status = exchangeResult.getStatus();
            if (status.is2xxSuccessful()) {
                log.info("주문 성공! : {}", new String(exchangeResult.getResponseBodyContent()));
                allowedCount++;
            }
            Thread.sleep(100);
        }
        log.info("allowed Count : {}", allowedCount);
    }

}
