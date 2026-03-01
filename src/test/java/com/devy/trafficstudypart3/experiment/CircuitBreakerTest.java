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

import java.time.ZonedDateTime;

@Import({TestClientConfig.class})
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CircuitBreakerTest {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private HttpClient httpClient;
    @LocalServerPort
    private String port;

    private RestClient testRestClient;

    private final int TIME = 10;
    private final String PATH = "/api/v1/circuit";

    @BeforeEach
    public void setUp() {
        testRestClient = RestClient.builder()
                .baseUrl("http://localhost:" + port)
                .requestFactory(new HttpComponentsClientHttpRequestFactory(httpClient))
                .build();
    }

    // fallback 응답이 잘 내려오는가?
    @DisplayName("fallback 응답이 잘 내려오는가?")
    @Test
    public void testCircuitBreaker() {
        log.info("start Request!");
        String body = testRestClient.get().uri(PATH + "?code={code}", "123456").retrieve().body(String.class);
        log.info("body : {}", body);
    }

    // 서킷이 열렸을떄 Fast Fail 이 되는가?
    @DisplayName("서킷이 열렸을떄 Fast Fail 이 되는가?")
    @Test
    public void testCircuitBreakerFastFail() throws InterruptedException {
        ZonedDateTime start = ZonedDateTime.now();
        while(ZonedDateTime.now().isBefore(start.plusSeconds(TIME))) {
            long startMillis = System.currentTimeMillis();
            String body = testRestClient.get().uri(PATH + "?code={code}", "fail").retrieve().body(String.class);
            log.info("body : {}, processed Time : {}", body, System.currentTimeMillis() - startMillis);
            Thread.sleep(100);
        }
    }


    // 정상상태를 감지하여 서킷을 다시 닫는가?
    @DisplayName("정상상태를 감지하여 서킷을 다시 닫는가?")
    @Test
    public void testCircuitBreakerClose() throws InterruptedException {
        ZonedDateTime start = ZonedDateTime.now();
        while(ZonedDateTime.now().isBefore(start.plusSeconds(TIME))) {
            long startMillis = System.currentTimeMillis();
            String body;
            if(ZonedDateTime.now().isBefore(start.plusSeconds(3))) {
                body = testRestClient.get().uri(PATH + "?code={code}", "fail").retrieve().body(String.class);
            }
            body = testRestClient.get().uri(PATH + "?code={code}", "success").retrieve().body(String.class);
            log.info("body : {}, processed Time : {}", body, System.currentTimeMillis() - startMillis);
            Thread.sleep(100);
        }
    }

}
