package com.example.hellostore.facade;

import com.example.hellostore.service.StockService;

import org.springframework.stereotype.Service;

@Service
public class OptimisticLockStockFacade {

    private final StockService stockService;

    public OptimisticLockStockFacade(final StockService stockService) {
        this.stockService = stockService;
    }

    public void decrease(
        final Long stockId,
        final Long purchaseQuantity
    ) throws InterruptedException {
        while (true) {
            try {
                stockService.decreaseStockWithOptimisticLock(stockId, purchaseQuantity);
                break;
            } catch (Exception e) {
                Thread.sleep(100);
            }
        }
    }
}
