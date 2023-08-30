package com.example.hellostore.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Product {

    @Id
    @Column(name = "product_id")
    private Long id;

    private String name;

    private Long price;

    protected Product() {
    }

    public Product(final Long id, final String name, final Long price) {
        this.id = id;
        this.name = name;
        this.price = price;
    }

    public Long getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public Long getPrice() {
        return this.price;
    }
}
