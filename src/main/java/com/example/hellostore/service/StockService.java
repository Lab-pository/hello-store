package com.example.hellostore.service;

import com.example.hellostore.domain.Stock;
import com.example.hellostore.repository.StockRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StockService {

    private final StockRepository stockRepository;

    public StockService(final StockRepository stockRepository) {
        this.stockRepository = stockRepository;
    }

    @Transactional(readOnly = true)
    public Long findStockWithPessimisticLock(final Long stockId) {
        final Stock stock = stockRepository.findById(stockId).orElseThrow(
                () -> new RuntimeException("123")
        );
        stock.decrease(1L);
        stockRepository.saveAndFlush(stock);
        return stock.getQuantity();
    }

    @Transactional
    public void decrease(final Long stockId, final Long purchaseQuantity) {
        final Stock stock = stockRepository.findById(stockId)
                .orElseThrow(IllegalArgumentException::new);

        stock.decrease(purchaseQuantity);
    }

    public synchronized void decreaseStockWithSynchronized(
            final Long stockId,
            final Long purchaseQuantity
    ) {
        final Stock stock = stockRepository.findById(stockId)
                .orElseThrow(IllegalArgumentException::new);

        stock.decrease(purchaseQuantity);

        stockRepository.saveAndFlush(stock);
    }

    @Transactional
    public void decreaseStockWithConsumeQuantity(final Long stockId, final Long purchaseQuantity) {
        stockRepository.consumeQuantity(stockId, purchaseQuantity);
    }

    @Transactional
    public void decreaseStockWithPessimisticLock(final Long stockId, final Long purchaseQuantity) {
        final Stock stock = stockRepository.findByIdWithPessimisticLock(stockId);

        stock.decrease(purchaseQuantity);

        stockRepository.saveAndFlush(stock);
    }

    @Transactional
    public void decreaseStockWithOptimisticLock(final Long stockId, final Long purchaseQuantity) {
        final Stock stock = stockRepository.findByIdWithOptimisticLock(stockId);

        stock.decrease(purchaseQuantity);

        stockRepository.saveAndFlush(stock);
    }
}
