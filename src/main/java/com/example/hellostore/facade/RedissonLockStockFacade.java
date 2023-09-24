package com.example.hellostore.facade;

import com.example.hellostore.service.RedissonLockStockService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

@Component
public class RedissonLockStockFacade {

    private final Logger logger = Logger.getLogger(RedissonLockStockFacade.class.getName());
    private final RedissonClient redissonClient;
    private final RedissonLockStockService stockService;

    public RedissonLockStockFacade(final RedissonClient redissonClient, final RedissonLockStockService stockService) {
        this.redissonClient = redissonClient;
        this.stockService = stockService;
    }

    public void decrease(final Long key, final Long quantity) {
        final RLock lock = redissonClient.getLock(key.toString());

        try {
            final boolean available = lock.tryLock(10, 1, TimeUnit.SECONDS);

            if (!available) {
                logger.info("Lock not available");
                return;
            }

            stockService.decrease(key, quantity);
        } catch (final InterruptedException e) {
            throw new IllegalStateException(e);
        } finally {
            lock.unlock();
        }
    }
}
