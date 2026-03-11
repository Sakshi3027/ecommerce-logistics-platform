package com.ecommerce.inventory_service.controller;

import com.ecommerce.inventory_service.dto.*;
import com.ecommerce.inventory_service.service.InventoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
@Slf4j
public class InventoryController {

    private final InventoryService inventoryService;

    // POST /api/inventory
    @PostMapping
    public ResponseEntity<InventoryResponse> addInventory(@Valid @RequestBody InventoryRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(inventoryService.addInventory(request));
    }

    // GET /api/inventory/product/{productId}
    @GetMapping("/product/{productId}")
    public ResponseEntity<InventoryResponse> getByProductId(@PathVariable Long productId) {
        return ResponseEntity.ok(inventoryService.getByProductId(productId));
    }

    // GET /api/inventory
    @GetMapping
    public ResponseEntity<List<InventoryResponse>> getAllInventory() {
        return ResponseEntity.ok(inventoryService.getAllInventory());
    }

    // GET /api/inventory/low-stock
    @GetMapping("/low-stock")
    public ResponseEntity<List<InventoryResponse>> getLowStockItems() {
        return ResponseEntity.ok(inventoryService.getLowStockItems());
    }

    // GET /api/inventory/out-of-stock
    @GetMapping("/out-of-stock")
    public ResponseEntity<List<InventoryResponse>> getOutOfStockItems() {
        return ResponseEntity.ok(inventoryService.getOutOfStockItems());
    }

    // PUT /api/inventory/add-stock
    @PutMapping("/add-stock")
    public ResponseEntity<InventoryResponse> addStock(@Valid @RequestBody StockUpdateRequest request) {
        return ResponseEntity.ok(inventoryService.addStock(request));
    }

    // PUT /api/inventory/deduct-stock
    @PutMapping("/deduct-stock")
    public ResponseEntity<InventoryResponse> deductStock(@Valid @RequestBody StockUpdateRequest request) {
        return ResponseEntity.ok(inventoryService.deductStock(request));
    }
}
