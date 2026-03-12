package com.ecommerce.warehouse_service.service;

import com.ecommerce.warehouse_service.dto.*;
import com.ecommerce.warehouse_service.entity.*;
import com.ecommerce.warehouse_service.enums.StockTransferStatus;
import com.ecommerce.warehouse_service.enums.WarehouseStatus;
import com.ecommerce.warehouse_service.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class WarehouseService {

    private final WarehouseRepository warehouseRepository;
    private final WarehouseProductRepository warehouseProductRepository;
    private final StockTransferRepository stockTransferRepository;

    @Transactional
    public WarehouseResponse createWarehouse(WarehouseRequest request) {
        log.info("Creating warehouse: {}", request.getWarehouseCode());
        Warehouse warehouse = Warehouse.builder()
                .warehouseCode(request.getWarehouseCode())
                .name(request.getName())
                .location(request.getLocation())
                .city(request.getCity())
                .state(request.getState())
                .totalCapacity(request.getTotalCapacity())
                .usedCapacity(0)
                .status(WarehouseStatus.ACTIVE)
                .build();
        return mapToResponse(warehouseRepository.save(warehouse));
    }

    public List<WarehouseResponse> getAllWarehouses() {
        return warehouseRepository.findAll()
                .stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    public WarehouseResponse getWarehouseById(Long id) {
        return mapToResponse(warehouseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Warehouse not found: " + id)));
    }

    @Transactional
    public WarehouseResponse addProductToWarehouse(AddProductRequest request) {
        Warehouse warehouse = warehouseRepository.findById(request.getWarehouseId())
                .orElseThrow(() -> new RuntimeException("Warehouse not found: " + request.getWarehouseId()));

        // Check if product already exists in warehouse
        var existing = warehouseProductRepository
                .findByWarehouseIdAndProductId(request.getWarehouseId(), request.getProductId());

        if (existing.isPresent()) {
            // Update quantity
            WarehouseProduct wp = existing.get();
            wp.setQuantity(wp.getQuantity() + request.getQuantity());
            warehouseProductRepository.save(wp);
        } else {
            // Add new product
            WarehouseProduct wp = WarehouseProduct.builder()
                    .warehouse(warehouse)
                    .productId(request.getProductId())
                    .productName(request.getProductName())
                    .quantity(request.getQuantity())
                    .shelfLocation(request.getShelfLocation())
                    .build();
            warehouseProductRepository.save(wp);
            warehouse.setUsedCapacity(warehouse.getUsedCapacity() + 1);
            warehouseRepository.save(warehouse);
        }

        log.info("Added product {} to warehouse {}", request.getProductId(), request.getWarehouseId());
        return mapToResponse(warehouse);
    }

    public List<WarehouseProduct> getProductsByWarehouse(Long warehouseId) {
        return warehouseProductRepository.findByWarehouseId(warehouseId);
    }

    public List<WarehouseProduct> getWarehousesByProduct(Long productId) {
        return warehouseProductRepository.findByProductId(productId);
    }

    @Transactional
    public StockTransferResponse transferStock(StockTransferRequest request) {
        log.info("Transferring {} units of product {} from warehouse {} to {}",
                request.getQuantity(), request.getProductId(),
                request.getSourceWarehouseId(), request.getDestinationWarehouseId());

        // Validate source has enough stock
        WarehouseProduct source = warehouseProductRepository
                .findByWarehouseIdAndProductId(
                        request.getSourceWarehouseId(), request.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found in source warehouse"));

        if (source.getQuantity() < request.getQuantity()) {
            throw new RuntimeException("Insufficient stock in source warehouse");
        }

        // Deduct from source
        source.setQuantity(source.getQuantity() - request.getQuantity());
        warehouseProductRepository.save(source);

        // Add to destination
        var dest = warehouseProductRepository
                .findByWarehouseIdAndProductId(
                        request.getDestinationWarehouseId(), request.getProductId());

        Warehouse destWarehouse = warehouseRepository.findById(request.getDestinationWarehouseId())
                .orElseThrow(() -> new RuntimeException("Destination warehouse not found"));

        if (dest.isPresent()) {
            WarehouseProduct dp = dest.get();
            dp.setQuantity(dp.getQuantity() + request.getQuantity());
            warehouseProductRepository.save(dp);
        } else {
            warehouseProductRepository.save(WarehouseProduct.builder()
                    .warehouse(destWarehouse)
                    .productId(request.getProductId())
                    .productName(request.getProductName())
                    .quantity(request.getQuantity())
                    .build());
        }

        // Record transfer
        StockTransfer transfer = StockTransfer.builder()
                .sourceWarehouseId(request.getSourceWarehouseId())
                .destinationWarehouseId(request.getDestinationWarehouseId())
                .productId(request.getProductId())
                .productName(request.getProductName())
                .quantity(request.getQuantity())
                .status(StockTransferStatus.COMPLETED)
                .notes(request.getNotes())
                .build();

        return mapTransferToResponse(stockTransferRepository.save(transfer));
    }

    public List<StockTransferResponse> getAllTransfers() {
        return stockTransferRepository.findAll()
                .stream().map(this::mapTransferToResponse).collect(Collectors.toList());
    }

    private WarehouseResponse mapToResponse(Warehouse w) {
        return WarehouseResponse.builder()
                .id(w.getId())
                .warehouseCode(w.getWarehouseCode())
                .name(w.getName())
                .location(w.getLocation())
                .city(w.getCity())
                .state(w.getState())
                .totalCapacity(w.getTotalCapacity())
                .usedCapacity(w.getUsedCapacity())
                .availableCapacity(w.getTotalCapacity() - w.getUsedCapacity())
                .status(w.getStatus())
                .createdAt(w.getCreatedAt())
                .build();
    }

    private StockTransferResponse mapTransferToResponse(StockTransfer t) {
        return StockTransferResponse.builder()
                .id(t.getId())
                .sourceWarehouseId(t.getSourceWarehouseId())
                .destinationWarehouseId(t.getDestinationWarehouseId())
                .productId(t.getProductId())
                .productName(t.getProductName())
                .quantity(t.getQuantity())
                .status(t.getStatus())
                .notes(t.getNotes())
                .createdAt(t.getCreatedAt())
                .build();
    }
}