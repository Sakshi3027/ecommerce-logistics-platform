package com.ecommerce.recommendation_service.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "product_purchases")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductPurchase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long customerId;

    @Column(nullable = false)
    private Long productId;

    @Column(nullable = false)
    private String productName;

    private String category;

    @Column(nullable = false)
    private Integer purchaseCount;

    @Column(nullable = false, updatable = false)
    private LocalDateTime firstPurchasedAt;

    private LocalDateTime lastPurchasedAt;

    @PrePersist
    protected void onCreate() {
        firstPurchasedAt = LocalDateTime.now();
        lastPurchasedAt = LocalDateTime.now();
        if (purchaseCount == null) purchaseCount = 1;
    }

    @PreUpdate
    protected void onUpdate() {
        lastPurchasedAt = LocalDateTime.now();
    }
}