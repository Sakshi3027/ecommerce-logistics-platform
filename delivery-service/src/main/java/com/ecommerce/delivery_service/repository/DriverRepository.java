package com.ecommerce.delivery_service.repository;

import com.ecommerce.delivery_service.entity.Driver;
import com.ecommerce.delivery_service.enums.DriverStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface DriverRepository extends JpaRepository<Driver, Long> {
    List<Driver> findByStatus(DriverStatus status);
    List<Driver> findByCurrentCity(String city);
    List<Driver> findByStatusAndCurrentCity(DriverStatus status, String city);
}