package com.ecommerce.recommendation_service.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "trending_products")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrendingProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Long productId;

    @Column(nullable = false)
    private String productName;

    private String category;

    @Column(nullable = false)
    private Integer totalPurchases;

    @Column(nullable = false)
    private Integer totalViews;

    private Double trendingScore;

    private LocalDateTime updatedAt;

    @PrePersist
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
        // Score = purchases * 3 + views * 1
        this.trendingScore = (totalPurchases * 3.0) + (totalViews * 1.0);
    }
}