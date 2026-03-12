package com.ecommerce.delivery_service.repository;

import com.ecommerce.delivery_service.entity.Delivery;
import com.ecommerce.delivery_service.enums.DeliveryStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface DeliveryRepository extends JpaRepository<Delivery, Long> {
    Optional<Delivery> findByOrderId(Long orderId);
    Optional<Delivery> findByDeliveryNumber(String deliveryNumber);
    List<Delivery> findByStatus(DeliveryStatus status);
    List<Delivery> findByDriverId(Long driverId);
    List<Delivery> findByCustomerId(Long customerId);
}