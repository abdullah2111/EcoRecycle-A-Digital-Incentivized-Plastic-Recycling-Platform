package com.example.ecorecycle.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * RecyclingHistory entity representing a single recycling transaction.
 * Links a user's recycling activity with a recycler, tracking plastic weight,
 * eco points earned, and pickup details.
 */
@Entity
@Table(
    name = "recycling_history",
    indexes = {
        @Index(name = "idx_user_id", columnList = "user_id"),
        @Index(name = "idx_recycler_id", columnList = "recycler_id"),
        @Index(name = "idx_pickup_date", columnList = "pickup_date")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"user", "recycler"})
public class RecyclingHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "history_id")
    private Long historyId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "recycler_id", nullable = false)
    private Recycler recycler;

    @Column(name = "plastic_weight", nullable = false)
    private Double plasticWeight;

    @Column(name = "eco_points_earned", nullable = false)
    @Builder.Default
    private Long ecoPointsEarned = 0L;

    @Column(name = "pickup_date", nullable = false)
    private LocalDateTime pickupDate;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "status", nullable = false)
    @Builder.Default
    private String status = "COMPLETED";

    @Column(name = "notes", length = 1000)
    private String notes;
}

