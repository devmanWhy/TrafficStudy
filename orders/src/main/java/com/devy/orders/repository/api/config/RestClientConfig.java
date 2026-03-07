package com.devy.orders.repository.api.config;

import com.devy.common.ApiInfo;
import org.apache.hc.client5.http.async.HttpAsyncClient;
import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.config.ConnectionConfig;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.async.CloseableHttpAsyncClient;
import org.apache.hc.client5.http.impl.async.HttpAsyncClients;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.impl.nio.PoolingAsyncClientConnectionManager;
import org.apache.hc.core5.reactor.IOReactorConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.util.concurrent.TimeUnit;

@Configuration
public class RestClientConfig {
    @Bean
    public HttpClient defaultHttpClient() {
        // Connection Pool 설정 + Connection Timeout 설정
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
        connectionManager.setMaxTotal(100); // Connection Total Size => 이 커넥션 풀이 가질 수 있는 최대 커넥션 수
        connectionManager.setDefaultMaxPerRoute(10); // Route당 최대 커넥션 수 => Route => 스키마://호스트:포트

        connectionManager.setDefaultConnectionConfig(
                ConnectionConfig.custom()
                        .setTimeToLive(1, TimeUnit.MINUTES) // 커넥션 유효시간
                        .setIdleTimeout(1, TimeUnit.MINUTES) // 풀에서 대기하는 최대시간
                        .setConnectTimeout(3, TimeUnit.SECONDS) // 커넥션 생성시 최대시간
                        .build()
        );
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(1L, TimeUnit.SECONDS)
                .setResponseTimeout(1L, TimeUnit.SECONDS) // Read Timeout 설정
                .build();

        return HttpClientBuilder.create()
                .disableAutomaticRetries()
                .setConnectionManager(connectionManager)
                .setDefaultRequestConfig(requestConfig)
                .build();
    }

    @Bean
    public HttpAsyncClient defaultHttpAsyncClient() {
        // Connection Pool 설정 + Connection Timeout 설정
        PoolingAsyncClientConnectionManager connectionManager =
                new PoolingAsyncClientConnectionManager();
        connectionManager.setMaxTotal(100);
        connectionManager.setDefaultMaxPerRoute(10);

        IOReactorConfig ioReactorConfig = IOReactorConfig.custom()
                .setSoTimeout(3, TimeUnit.SECONDS)
                .build();

        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(1, TimeUnit.SECONDS)
                .setResponseTimeout(1, TimeUnit.SECONDS)
                .build();

        CloseableHttpAsyncClient client = HttpAsyncClients.custom()
                .setConnectionManager(connectionManager)
                .setIOReactorConfig(ioReactorConfig)
                .setDefaultRequestConfig(requestConfig)
                .build();

        client.start();

        return client;
    }

    @Bean
    public RestClient productsRestClient(HttpClient httpClient) {
        return RestClient.builder()
                .baseUrl(ApiInfo.PRODUCTS.BASE_URL)
                .requestFactory(new HttpComponentsClientHttpRequestFactory(httpClient))
                .build();
    }

    @Bean
    public RestClient paymentsRestClient(HttpClient httpClient) {
        return RestClient.builder()
                .baseUrl(ApiInfo.PAYMENTS.BASE_URL)
                .requestFactory(new HttpComponentsClientHttpRequestFactory(httpClient))
                .build();
    }

}
