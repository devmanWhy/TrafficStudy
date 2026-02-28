package com.devy.trafficstudypart3.experiment;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.web.servlet.client.RestTestClient;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ThreadTest {

    //--> 요청을 보낼 준비
    private final int CONCURRENCY = 10;

    private RestTestClient client;

    private Logger log = LoggerFactory.getLogger(ThreadTest.class);

    @LocalServerPort
    private String port;

    @BeforeEach
    public void setUp() {
        client = RestTestClient.bindToServer().baseUrl("http://localhost:" + port)
                .build();
    }

    @DisplayName("동시 요청 수 만큼 Thread 가 늘어나는지 확인")
    @Test
    public void testThreadCountByRequests() {

        ExecutorService executorService = Executors.newFixedThreadPool(CONCURRENCY);

        try (executorService) {
            for (int index = 0; index < CONCURRENCY; index++) {
                executorService.submit(() -> {
                    // 요청할 API Path 및 파라미터 혹은 바디 설정
                    RestTestClient.ResponseSpec responseSpec = client.get().uri("/api/v1/threads").exchange();
                    responseSpec.expectStatus().isOk();
                });
            }
        }

        log.info("모든 요청 처리 완료!");
    }
}
