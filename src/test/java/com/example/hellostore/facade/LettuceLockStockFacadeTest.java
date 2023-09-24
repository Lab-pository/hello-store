package com.example.hellostore.facade;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchException;

import com.example.hellostore.TestHelper;
import com.example.hellostore.domain.Stock;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class LettuceLockStockFacadeTest extends TestHelper {

    @Autowired
    private LettuceLockStockFacade lettuceLockStockFacade;

    @Test
    void 동시에_1000개의_요청() throws InterruptedException {
        final ExecutorService executorService = Executors.newFixedThreadPool(N_THREADS);
        final CountDownLatch latch = new CountDownLatch(THREAD_COUNT);

        final AtomicInteger successCount = new AtomicInteger(0);
        final AtomicInteger failCount = new AtomicInteger(0);

        for (int i = 0; i < THREAD_COUNT; i++) {
            executorService.submit(() -> {
                try {
                    lettuceLockStockFacade.decrease(1L, 1L);
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

    @Test
    void 개수를_초과하는_경우() {
        final Exception exception = catchException(() -> lettuceLockStockFacade.decrease(1L, 501L));

        assertThat(exception).isInstanceOf(IllegalStateException.class);
    }
}
