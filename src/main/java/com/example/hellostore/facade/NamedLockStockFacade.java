package com.example.hellostore.facade;

import com.example.hellostore.repository.LockRepository;
import com.example.hellostore.service.StockServiceV2;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class NamedLockStockFacade {

    private final StockServiceV2 stockService;
    private final LockRepository lockRepository;

    public NamedLockStockFacade(
        final StockServiceV2 stockService,
        final LockRepository lockRepository
    ) {
        this.stockService = stockService;
        this.lockRepository = lockRepository;
    }

    @Transactional
    public void decrease(final Long stockId, final Long purchaseQuantity) {
        try {
            lockRepository.getLock(stockId.toString());

            stockService.decrease(stockId, purchaseQuantity);
        } finally {
            lockRepository.releaseLock(stockId.toString());
        }
    }
}
