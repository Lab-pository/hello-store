package com.example.hellostore.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Version;

@Entity
public class Stock {

    @Id
    @Column(name = "stock_id")
    private Long id;

    @Column(name = "product_id", unique = true, nullable = false)
    private Long productId;

    @Column(name = "quantity", nullable = false)
    private Long quantity;

    @Version
    private Long version;

    protected Stock() {
    }

    public Stock(final Long id, final Long productId, final Long quantity) {
        this.id = id;
        this.productId = productId;
        this.quantity = quantity;
    }

    public void decrease(final Long quantity) {
        if (this.quantity - quantity < 0) {
            throw new IllegalStateException("재고는 0개 미만이 될 수 없습니다.");
        }
        this.quantity -= quantity;
    }

    public Long getId() {
        return this.id;
    }

    public Long getProductId() {
        return this.productId;
    }

    public Long getQuantity() {
        return this.quantity;
    }
}
