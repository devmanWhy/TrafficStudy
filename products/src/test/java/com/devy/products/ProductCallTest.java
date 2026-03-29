package com.devy.products;

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

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SpringBootTest(classes = {RestTestClient.class})
public class ProductCallTest {

    private RestTestClient client;

    private Logger log = LoggerFactory.getLogger(ProductCallTest.class);

    @BeforeEach
    public void setUp() {
        client = RestTestClient.bindToServer().baseUrl("http://localhost:8081")
                .build();
    }

    @DisplayName("동시 상품 조회 트래픽")
    @Test
    public void testSearchInventoryParallelTraffic() {
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        try (executorService) {
            for (int index = 1; index <= 10; index++) {
                executorService.submit(() -> {
                    RestTestClient.ResponseSpec responseSpec = client.get().uri(ApiInfo.PRODUCTS.BASE_PATH + "/inventory"
                                    + "/product-1")
                            .exchange();
                    ExchangeResult exchangeResult = responseSpec.returnResult();
                    HttpStatusCode statusCode = exchangeResult.getStatus();
                    if (statusCode.is2xxSuccessful()) {
                        log.info("인벤토리 조회 성공! {}", new String(exchangeResult.getResponseBodyContent()));
                    }
                });
            }
        }
        log.info("Done!!!");
    }

}
