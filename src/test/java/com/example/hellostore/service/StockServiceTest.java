package com.example.hellostore.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchException;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.example.hellostore.domain.Stock;
import com.example.hellostore.repository.StockRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class StockServiceTest {

    private static final int THREAD_COUNT = 1000;

    @Autowired
    private StockService stockService;

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
    void 한번_요청시_1개_재고_감소_Synchronized() {
        stockService.decreaseStockWithSynchronized(1L, 1L);

        final Stock stock = stockRepository.findById(1L).orElseThrow();

        assertEquals(499, stock.getQuantity());
    }

    @Test
    void 한번_요청시_재고_감소_Consume() {
        stockService.decreaseStockWithConsumeQuantity(1L, 1L);

        final Stock stock = stockRepository.findById(1L).orElseThrow();

        assertEquals(499, stock.getQuantity());
    }

    @Test
    void 한번_요청시_재고_감소를_0이하로_하는_경우_Consume() {
        final Exception exception = catchException(
            () -> stockService.decreaseStockWithConsumeQuantity(1L, 1000L));

        assertThat(exception).isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void 동시에_1000개요청_재고_감소_Synchronized() throws InterruptedException {
        final ExecutorService executorService = Executors.newFixedThreadPool(64);
        final CountDownLatch latch = new CountDownLatch(THREAD_COUNT);

        for (int i = 0; i < THREAD_COUNT; i++) {
            executorService.submit(() -> {
                try {
                    stockService.decreaseStockWithSynchronized(1L, 1L);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        final Stock stock = stockRepository.findById(1L).orElseThrow();
        assertThat(stock.getQuantity()).isZero();
    }

    @Test
    void 동시에_1000개의_요청_Consume() throws InterruptedException {
        final ExecutorService executorService = Executors.newFixedThreadPool(64);
        final CountDownLatch latch = new CountDownLatch(THREAD_COUNT);

        List<Integer> success = new ArrayList<>();
        List<Integer> fail = new ArrayList<>();
        for (int i = 0; i < THREAD_COUNT; i++) {
            executorService.submit(() -> {
                try {
                    stockService.decreaseStockWithConsumeQuantity(1L, 1L);
                    success.add(1);
                } catch(Throwable e) {
                    fail.add(1);
                }finally {
                    latch.countDown();
                }
            });
        }
        latch.await();

        System.out.println("success.size() " + success.size());
        System.out.println("fail.size() " + fail.size());
        final Stock stock = stockRepository.findById(1L).orElseThrow();
        assertThat(stock.getQuantity()).isZero();
    }

    @Test
    void 동시에_1000개의_요청_PessimisticLock() throws InterruptedException {
        final ExecutorService executorService = Executors.newFixedThreadPool(64);
        final CountDownLatch latch = new CountDownLatch(THREAD_COUNT);

        List<Integer> success = new ArrayList<>();
        List<Integer> fail = new ArrayList<>();
        for (int i = 0; i < THREAD_COUNT; i++) {
            executorService.submit(() -> {
                try {
                    stockService.decreaseStockWithPessimisticLock(1L, 1L);
                    success.add(1);
                } catch(Throwable e) {
                    fail.add(1);
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();

        System.out.println("success.size() " + success.size());
        System.out.println("fail.size() " + fail.size());
        final Stock stock = stockRepository.findById(1L).orElseThrow();
        assertThat(stock.getQuantity()).isZero();
    }
}
