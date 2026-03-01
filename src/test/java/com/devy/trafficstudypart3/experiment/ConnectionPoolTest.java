package com.devy.trafficstudypart3.experiment;

import com.devy.trafficstudypart3.experiment.config.TestClientConfig;
import org.apache.hc.client5.http.classic.HttpClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertThrows;

@Import({TestClientConfig.class})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ConnectionPoolTest {

    private Logger log = LoggerFactory.getLogger(ConnectionPoolTest.class);


    @Autowired
    private HttpClient httpClient;

    @LocalServerPort
    private String port;

    private RestClient testRestClient;

    @BeforeEach
    public void setUp() {
        testRestClient = RestClient.builder()
                .baseUrl("http://localhost:" + port)
                .requestFactory(new HttpComponentsClientHttpRequestFactory(httpClient))
                .build();
    }

    // Read Timeout 이 잘 작동하는지
    @DisplayName("Read Timeout 아웃이 잘 작동하는지 - timeout 1초")
    @Test
    public void testReadTimeout() {
        Exception exception = assertThrows(Exception.class, () -> {
            RestClient.ResponseSpec retrieve = testRestClient.get()
                    .uri("/api/v1/simulation?duration={duration}&size={size}"
                            , 2000, 1)
                    .retrieve();
            System.out.println(retrieve.body(String.class));
        });
        exception.printStackTrace();

    }

    // Route 별 Bulk Head 가 잘 작동하는지
    @DisplayName("Route 별 Bulk Head 가 잘 작동하는지 - Bulk Head 1")
    @Test
    public void testBulkHead() {
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        try (executorService) {
            for (int index = 0; index < 2; index++) {
                executorService.submit(() -> {
                    log.info("요청시작!");
                    RestClient.ResponseSpec retrieve = testRestClient.get()
                            .uri("/api/v1/simulation?duration={duration}&size={size}"
                                    , 500, 1)
                            .retrieve();
                    log.info(retrieve.body(String.class));
                });
            }
        }
        log.info("모든 요청 처리 완료!");
    }

    // Connection 사용 최대 대기시간 잘 작동하는지
    @DisplayName("Connection 사용 최대 대기시간 잘 작동하는지 - Bulk Head 1, borrow 1초")
    @Test
    public void testConnectionBorrow() {
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        try (executorService) {
            for (int index = 0; index < 2; index++) {
                executorService.submit(() -> {
                    try {
                        log.info("요청시작!");
                        RestClient.ResponseSpec retrieve = testRestClient.get()
                                .uri("/api/v1/simulation?duration={duration}&size={size}"
                                        , 1500, 1)
                                .retrieve();
                        log.info(retrieve.body(String.class));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }
        }
        log.info("모든 요청 처리 완료!");
    }


}
