package com.ecommerce.warehouse_service.controller;

import com.ecommerce.warehouse_service.dto.*;
import com.ecommerce.warehouse_service.entity.WarehouseProduct;
import com.ecommerce.warehouse_service.service.WarehouseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/warehouses")
@RequiredArgsConstructor
public class WarehouseController {

    private final WarehouseService warehouseService;

    @PostMapping
    public ResponseEntity<WarehouseResponse> createWarehouse(@Valid @RequestBody WarehouseRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(warehouseService.createWarehouse(request));
    }

    @GetMapping
    public ResponseEntity<List<WarehouseResponse>> getAllWarehouses() {
        return ResponseEntity.ok(warehouseService.getAllWarehouses());
    }

    @GetMapping("/{id}")
    public ResponseEntity<WarehouseResponse> getWarehouseById(@PathVariable Long id) {
        return ResponseEntity.ok(warehouseService.getWarehouseById(id));
    }

    @PostMapping("/products")
    public ResponseEntity<WarehouseResponse> addProduct(@Valid @RequestBody AddProductRequest request) {
        return ResponseEntity.ok(warehouseService.addProductToWarehouse(request));
    }

    @GetMapping("/{warehouseId}/products")
    public ResponseEntity<List<WarehouseProduct>> getProductsByWarehouse(@PathVariable Long warehouseId) {
        return ResponseEntity.ok(warehouseService.getProductsByWarehouse(warehouseId));
    }

    @GetMapping("/products/{productId}/locations")
    public ResponseEntity<List<WarehouseProduct>> getWarehousesByProduct(@PathVariable Long productId) {
        return ResponseEntity.ok(warehouseService.getWarehousesByProduct(productId));
    }

    @PostMapping("/transfer")
    public ResponseEntity<StockTransferResponse> transferStock(@Valid @RequestBody StockTransferRequest request) {
        return ResponseEntity.ok(warehouseService.transferStock(request));
    }

    @GetMapping("/transfers")
    public ResponseEntity<List<StockTransferResponse>> getAllTransfers() {
        return ResponseEntity.ok(warehouseService.getAllTransfers());
    }
}