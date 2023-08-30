package com.example.hellostore.controller;

import com.example.hellostore.service.StockService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StockController {

    private final StockService stockService;

    public StockController(final StockService stockService) {
        this.stockService = stockService;
    }

    @PostMapping("/stocks/{stockId}")
    public ResponseEntity<Void> decreaseStocks(
        @PathVariable final Long stockId,
        @RequestParam final Long quantity
    ) {
        stockService.decreaseStockWithSynchronized(stockId, quantity);

        return ResponseEntity.ok().build();
    }
}

