package com.example.hellostore.facade;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.hellostore.domain.Stock;
import com.example.hellostore.repository.StockRepository;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class NamedLockStockFacadeTest {

    private static final int THREAD_COUNT = 1000;

    @Autowired
    private NamedLockStockFacade namedLockStockFacade;

    @Autowired
    private StockRepository stockRepository;

    @BeforeEach
    void setUp() {
        final Stock stock = new Stock(1L, 1L, 500L);

        stockRepository.saveAndFlush(stock);
    }

    @AfterEach
    void tearDown() {
        stockRepository.deleteById(1L);
    }

    @Test
    void 동시에_1000개의_요청() throws InterruptedException {
        final ExecutorService executorService = Executors.newFixedThreadPool(64);
        final CountDownLatch latch = new CountDownLatch(THREAD_COUNT);

        final AtomicInteger successCount = new AtomicInteger(0);
        final AtomicInteger failCount = new AtomicInteger(0);

        for (int i = 0; i < THREAD_COUNT; i++) {
            executorService.submit(() -> {
                try {
                    namedLockStockFacade.decrease(1L, 1L);
                    successCount.getAndIncrement();
                } catch (Exception e) {
                    failCount.getAndIncrement();
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();

        final Stock stock = stockRepository.findById(1L).orElseThrow();
        assertThat(stock.getQuantity()).isZero();
        assertThat(successCount.get()).isEqualTo(500);
        assertThat(failCount.get()).isEqualTo(500);
    }
}
