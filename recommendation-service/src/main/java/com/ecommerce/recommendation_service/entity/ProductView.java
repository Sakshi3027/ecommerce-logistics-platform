package com.ecommerce.recommendation_service.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "product_views")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductView {

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
    private Integer viewCount;

    @Column(nullable = false, updatable = false)
    private LocalDateTime firstViewedAt;

    private LocalDateTime lastViewedAt;

    @PrePersist
    protected void onCreate() {
        firstViewedAt = LocalDateTime.now();
        lastViewedAt = LocalDateTime.now();
        if (viewCount == null) viewCount = 1;
    }

    @PreUpdate
    protected void onUpdate() {
        lastViewedAt = LocalDateTime.now();
    }
}