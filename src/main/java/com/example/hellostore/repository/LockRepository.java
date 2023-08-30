package com.example.hellostore.repository;

import com.example.hellostore.domain.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface LockRepository extends JpaRepository<Stock, Long> {

    @Query(value = "SELECT GET_LOCK(:key, 1000)", nativeQuery = true)
    void getLock(final String key);

    @Query(value = "SELECT IS_FREE_LOCK(:key)", nativeQuery = true)
    void isFreeLock(final String key);

    @Query(value = "SELECT RELEASE_LOCK(:key)", nativeQuery = true)
    void releaseLock(final String key);
}
