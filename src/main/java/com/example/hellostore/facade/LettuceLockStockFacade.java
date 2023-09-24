package com.example.hellostore.facade;

import com.example.hellostore.repository.LettuceLockRepository;
import com.example.hellostore.service.LettuceLockStockService;
import org.springframework.stereotype.Component;

@Component
public class LettuceLockStockFacade {

    private final LettuceLockRepository repository;
    private final LettuceLockStockService stockService;

    public LettuceLockStockFacade(final LettuceLockRepository repository, final LettuceLockStockService stockService) {
        this.repository = repository;
        this.stockService = stockService;
    }

    public void decrease(final Long key, final Long quantity) {
        try {
            while (!repository.lock(key)) {
                Thread.sleep(50);
            }
            stockService.decrease(key, quantity);
        } catch (final InterruptedException e) {
            throw new IllegalStateException(e);
        } finally {
            repository.unlock(key);
        }
    }
}
