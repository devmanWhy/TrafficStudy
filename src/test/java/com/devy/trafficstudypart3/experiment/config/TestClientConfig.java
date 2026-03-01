package com.devy.trafficstudypart3.experiment.config;

import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.config.ConnectionConfig;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import java.util.concurrent.TimeUnit;

@TestConfiguration
public class TestClientConfig {

    @Bean
    public HttpClient defaultHttpClient() {
        // Connection Pool 설정 + Connection Timeout 설정
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
        connectionManager.setMaxTotal(100); // Connection Total Size => 이 커넥션 풀이 가질 수 있는 최대 커넥션 수
        connectionManager.setDefaultMaxPerRoute(10); // Route당 최대 커넥션 수 => Route => 스키마://호스트:포트

        // 전체 사용가능한 커넥션은 5개이고 라우트 당 1개까지만 사용가능
        connectionManager.setDefaultConnectionConfig(
                ConnectionConfig.custom()
                        .setTimeToLive(1, TimeUnit.MINUTES) // 커넥션 유효시간
                        .setIdleTimeout(1, TimeUnit.MINUTES) // 풀에서 대기하는 최대시간
                        .setConnectTimeout(3, TimeUnit.SECONDS) // 커넥션 생성시 최대시간
                        .build()
        );
        // 요까지는 Connection 관련 설정
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(3L, TimeUnit.SECONDS)
                .setResponseTimeout(2L, TimeUnit.SECONDS) // Read Timeout 설정
                .build();

        return HttpClientBuilder.create()
                .disableAutomaticRetries() // 자동으로 리트라이 비활성화
                .setConnectionManager(connectionManager)
                .setDefaultRequestConfig(requestConfig)
                .build();
    }
}
