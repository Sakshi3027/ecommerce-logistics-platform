package com.ecommerce.inventory_service.repository;

import com.ecommerce.inventory_service.entity.Inventory;
import com.ecommerce.inventory_service.enums.InventoryStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {

    Optional<Inventory> findByProductId(Long productId);

    List<Inventory> findByStatus(InventoryStatus status);

    List<Inventory> findByQuantityAvailableLessThanEqual(Integer threshold);
}