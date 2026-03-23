package com.devy.orders.times;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CalculateTimeTest {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @DisplayName("File 과 Memory 속도 차이")
    @Test
    public void compareFileAndMemory() throws IOException {
        Map<String, String> tempMap = new HashMap<String, String>();
        Path tempFile = Files.createTempFile("testFile", ".txt");
        tempFile.toFile().deleteOnExit();
        Files.writeString(tempFile, "Hello World File!!!");
        tempMap.put("key01", "Hello World Memory!!!");

        long startTime = System.nanoTime();
        String content = Files.readString(tempFile);
        log.info("Read Content From File : {}, Time : {}", content, System.nanoTime() - startTime);

        startTime = System.nanoTime();
        String contentFromMemory = tempMap.get("key01");
        log.info("Read Content From Memory : {}, Time : {}", contentFromMemory, System.nanoTime() - startTime);
    }

    @DisplayName("Single Thread, Multi Thread 연산 차이")
    @Test
    public void compareSingleThreadAndMultiThread() throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        // Thread 1개 :  352684333
        // Thread 10개 : 752483667

        int count = 10_000_000;
        CountDownLatch countDownLatch = new CountDownLatch(count);
        long startTime = System.nanoTime();
        for (int index = 0; index < count; index++) {
            executorService.execute(countDownLatch::countDown);
        }
        countDownLatch.await();
        log.info("Count Complete : {}, Time : {}", count,  System.nanoTime() - startTime);
    }

}
