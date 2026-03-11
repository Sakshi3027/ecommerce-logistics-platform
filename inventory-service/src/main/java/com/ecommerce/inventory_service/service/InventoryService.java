package com.ecommerce.inventory_service.service;

import com.ecommerce.inventory_service.dto.*;
import com.ecommerce.inventory_service.entity.Inventory;
import com.ecommerce.inventory_service.enums.InventoryStatus;
import com.ecommerce.inventory_service.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryService {

    private final InventoryRepository inventoryRepository;

    @Transactional
    public InventoryResponse addInventory(InventoryRequest request) {
        log.info("Adding inventory for product: {}", request.getProductId());

        if (inventoryRepository.findByProductId(request.getProductId()).isPresent()) {
            throw new RuntimeException("Inventory already exists for product: " + request.getProductId());
        }

        Inventory inventory = Inventory.builder()
                .productId(request.getProductId())
                .productName(request.getProductName())
                .quantityAvailable(request.getQuantityAvailable())
                .quantityReserved(0)
                .lowStockThreshold(request.getLowStockThreshold())
                .warehouseLocation(request.getWarehouseLocation())
                .status(InventoryStatus.IN_STOCK)
                .build();

        inventory.updateStatus();
        return mapToResponse(inventoryRepository.save(inventory));
    }

    public InventoryResponse getByProductId(Long productId) {
        Inventory inventory = inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new RuntimeException("Inventory not found for product: " + productId));
        return mapToResponse(inventory);
    }

    public List<InventoryResponse> getAllInventory() {
        return inventoryRepository.findAll()
                .stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    public List<InventoryResponse> getLowStockItems() {
        return inventoryRepository.findByStatus(InventoryStatus.LOW_STOCK)
                .stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    public List<InventoryResponse> getOutOfStockItems() {
        return inventoryRepository.findByStatus(InventoryStatus.OUT_OF_STOCK)
                .stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Transactional
    public InventoryResponse addStock(StockUpdateRequest request) {
        Inventory inventory = inventoryRepository.findByProductId(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Inventory not found for product: " + request.getProductId()));

        log.info("Adding {} units to product: {}", request.getQuantity(), request.getProductId());
        inventory.setQuantityAvailable(inventory.getQuantityAvailable() + request.getQuantity());
        inventory.updateStatus();
        return mapToResponse(inventoryRepository.save(inventory));
    }

    @Transactional
    public InventoryResponse deductStock(StockUpdateRequest request) {
        Inventory inventory = inventoryRepository.findByProductId(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Inventory not found for product: " + request.getProductId()));

        if (inventory.getQuantityAvailable() < request.getQuantity()) {
            throw new RuntimeException("Insufficient stock for product: " + request.getProductId());
        }

        log.info("Deducting {} units from product: {}", request.getQuantity(), request.getProductId());
        inventory.setQuantityAvailable(inventory.getQuantityAvailable() - request.getQuantity());
        inventory.updateStatus();
        return mapToResponse(inventoryRepository.save(inventory));
    }

    @Transactional
    public void deductStockForOrder(Long productId, Integer quantity) {
        inventoryRepository.findByProductId(productId).ifPresent(inventory -> {
            if (inventory.getQuantityAvailable() >= quantity) {
                inventory.setQuantityAvailable(inventory.getQuantityAvailable() - quantity);
                inventory.updateStatus();
                inventoryRepository.save(inventory);
                log.info("Auto-deducted {} units for product {} via Kafka event", quantity, productId);
            } else {
                log.warn("Insufficient stock for product {} — requested: {}, available: {}",
                        productId, quantity, inventory.getQuantityAvailable());
            }
        });
    }

    private InventoryResponse mapToResponse(Inventory inventory) {
        return InventoryResponse.builder()
                .id(inventory.getId())
                .productId(inventory.getProductId())
                .productName(inventory.getProductName())
                .quantityAvailable(inventory.getQuantityAvailable())
                .quantityReserved(inventory.getQuantityReserved())
                .lowStockThreshold(inventory.getLowStockThreshold())
                .status(inventory.getStatus())
                .warehouseLocation(inventory.getWarehouseLocation())
                .createdAt(inventory.getCreatedAt())
                .updatedAt(inventory.getUpdatedAt())
                .build();
    }
}