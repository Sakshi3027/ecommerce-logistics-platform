package com.ecommerce.warehouse_service.repository;

import com.ecommerce.warehouse_service.entity.Warehouse;
import com.ecommerce.warehouse_service.enums.WarehouseStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface WarehouseRepository extends JpaRepository<Warehouse, Long> {
    Optional<Warehouse> findByWarehouseCode(String warehouseCode);
    List<Warehouse> findByStatus(WarehouseStatus status);
    List<Warehouse> findByCity(String city);
}