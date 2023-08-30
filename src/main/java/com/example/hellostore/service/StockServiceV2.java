package com.example.hellostore.service;

import com.example.hellostore.domain.Stock;
import com.example.hellostore.repository.StockRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StockServiceV2 {

    private final StockRepository stockRepository;

    public StockServiceV2(final StockRepository stockRepository) {
        this.stockRepository = stockRepository;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void decrease(final Long stockId, final Long purchaseQuantity) {
        final Stock stock = stockRepository.findById(stockId)
            .orElseThrow(IllegalArgumentException::new);

        stock.decrease(purchaseQuantity);

        stockRepository.saveAndFlush(stock);
    }
}
