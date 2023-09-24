package com.example.hellostore;

import com.example.hellostore.domain.Stock;
import com.example.hellostore.repository.StockRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public abstract class TestHelper {

    protected static final int THREAD_COUNT = 1000;
    protected static final int N_THREADS = 32;

    @Autowired
    protected StockRepository stockRepository;

    @BeforeEach
    public void before() {
        final Stock stock = new Stock(1L, 1L, 500L);

        stockRepository.saveAndFlush(stock);
    }

    @AfterEach
    void tearDown() {
        stockRepository.deleteById(1L);
    }
}
