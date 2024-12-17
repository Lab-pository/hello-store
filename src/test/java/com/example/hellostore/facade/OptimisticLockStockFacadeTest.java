package com.example.hellostore.facade;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.example.hellostore.TestHelper;
import com.example.hellostore.domain.Stock;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class OptimisticLockStockFacadeTest extends TestHelper {

    @Autowired
    private OptimisticLockStockFacade optimisticLockStockFacade;

    @Test
    void 한번_요청시_1개_재고_감소_OptimisticLock() throws InterruptedException {
        optimisticLockStockFacade.decrease(1L, 1L);

        final Stock stock = stockRepository.findById(1L).orElseThrow();

        assertEquals(499L, stock.getQuantity());
    }

    @Test
    void 동시에_1000개의_요청_OptimisticLock() throws InterruptedException {
        final ExecutorService executorService = Executors.newFixedThreadPool(N_THREADS);
        final CountDownLatch latch = new CountDownLatch(THREAD_COUNT);

        final AtomicInteger successCount = new AtomicInteger(0);
        final AtomicInteger failCount = new AtomicInteger(0);

        for (int i = 0; i < THREAD_COUNT; i++) {
            executorService.submit(() -> {
                try {
                    optimisticLockStockFacade.decrease(1L, 1L);
                    successCount.getAndIncrement();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
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
