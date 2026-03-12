package com.ecommerce.warehouse_service.repository;

import com.ecommerce.warehouse_service.entity.WarehouseProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface WarehouseProductRepository extends JpaRepository<WarehouseProduct, Long> {
    List<WarehouseProduct> findByWarehouseId(Long warehouseId);
    Optional<WarehouseProduct> findByWarehouseIdAndProductId(Long warehouseId, Long productId);
    List<WarehouseProduct> findByProductId(Long productId);
}