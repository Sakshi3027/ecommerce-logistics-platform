package com.ecommerce.warehouse_service.repository;

import com.ecommerce.warehouse_service.entity.StockTransfer;
import com.ecommerce.warehouse_service.enums.StockTransferStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface StockTransferRepository extends JpaRepository<StockTransfer, Long> {
    List<StockTransfer> findBySourceWarehouseId(Long warehouseId);
    List<StockTransfer> findByDestinationWarehouseId(Long warehouseId);
    List<StockTransfer> findByStatus(StockTransferStatus status);
}