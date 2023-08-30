package com.example.hellostore.repository;


import com.example.hellostore.domain.Stock;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface StockRepository extends JpaRepository<Stock, Long> {

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("update Stock as s set s.quantity = s.quantity - :quantity where s.id = :stockId")
    void consumeQuantity(
        @Param("stockId") final Long stockId,
        @Param("quantity") final Long quantity
    );

    @Lock(value = LockModeType.PESSIMISTIC_WRITE)
    @Query("select s from Stock s where s.id = :stockId")
    Stock findByIdWithPessimisticLock(@Param("stockId") final Long stockId);

    @Lock(value = LockModeType.OPTIMISTIC)
    @Query("select s from Stock s where s.id = :stockId")
    Stock findByIdWithOptimisticLock(@Param("stockId") final Long stockId);
}
