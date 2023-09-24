package com.example.hellostore.service;

import com.example.hellostore.domain.Stock;
import com.example.hellostore.repository.StockRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LettuceLockStockService {

    private final StockRepository stockRepository;

    public LettuceLockStockService(final StockRepository stockRepository) {
        this.stockRepository = stockRepository;
    }

    @Transactional
    public void decrease(final Long id, final Long quantity) {
        final Stock stock = stockRepository.findById(id).orElseThrow();

        stock.decrease(quantity);

        stockRepository.saveAndFlush(stock);
    }
}
