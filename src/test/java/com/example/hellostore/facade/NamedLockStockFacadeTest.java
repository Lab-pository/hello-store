package com.example.hellostore.facade;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.hellostore.domain.Stock;
import com.example.hellostore.repository.StockRepository;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class NamedLockStockFacadeTest {

    private static final int THREAD_COUNT = 100;

    @Autowired
    private NamedLockStockFacade namedLockStockFacade;

    @Autowired
    private StockRepository stockRepository;

    @BeforeEach
    void setUp() {
        final Stock stock = new Stock(1L, 1L, 100L);

        stockRepository.saveAndFlush(stock);
    }

    @AfterEach
    void tearDown() {
        stockRepository.deleteById(1L);
    }

    @Test
    void 동시에_100개의_요청() throws InterruptedException {
        final ExecutorService executorService = Executors.newFixedThreadPool(64);
        final CountDownLatch latch = new CountDownLatch(THREAD_COUNT);

        for (int i = 0; i < THREAD_COUNT; i++) {
            executorService.submit(() -> {
                try {
                    namedLockStockFacade.decrease(1L, 1L);
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();

        final Stock stock = stockRepository.findById(1L).orElseThrow();
        assertThat(stock.getQuantity()).isZero();
    }
}
