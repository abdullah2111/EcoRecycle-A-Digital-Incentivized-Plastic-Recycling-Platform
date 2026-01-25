package com.example.ecorecycle.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * EcoGift entity - Eco-friendly products available for redemption
 */
@Entity
@Table(name = "eco_gifts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EcoGift {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "gift_id")
    private Long giftId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "eco_point_price", nullable = false)
    private Long ecoPointPrice;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "category", length = 100)
    private String category;

    @Column(name = "is_available", nullable = false)
    @Builder.Default
    private Boolean isAvailable = true;

    @Column(name = "stock_quantity")
    private Integer stockQuantity;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();
}

