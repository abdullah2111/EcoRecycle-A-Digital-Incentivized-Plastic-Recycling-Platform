package com.example.ecorecycle.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Recycler entity representing a recycling center or service provider.
 * Manages recycling operations, schedules, and capacity for plastic waste collection.
 */
@Entity
@Table(
    name = "recyclers",
    indexes = {
        @Index(name = "idx_service_area", columnList = "service_area"),
        @Index(name = "idx_ratings", columnList = "ratings")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = "recyclingHistories")
@EqualsAndHashCode(exclude = "recyclingHistories")
public class Recycler {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "recycler_id")
    private Long recyclerId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "email")
    private String email;

    @Column(name = "phone")
    private String phone;

    @Column(name = "service_area", nullable = false, length = 500)
    private String serviceArea;

    @Column(name = "pickup_schedule", length = 1000)
    private String pickupSchedule;

    @Column(name = "recycling_capacity", nullable = false)
    private Double recyclingCapacity;

    @Column(name = "ratings")
    @Builder.Default
    private Double ratings = 0.0;

    @Column(name = "total_reviews")
    @Builder.Default
    private Integer totalReviews = 0;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "address", length = 500)
    private String address;

    @Column(name = "operating_hours")
    private String operatingHours;

    @Column(name = "accepted_plastic_types", length = 1000)
    private String acceptedPlasticTypes;

    @OneToMany(
        mappedBy = "recycler",
        cascade = CascadeType.ALL,
        orphanRemoval = true,
        fetch = FetchType.LAZY
    )
    @Builder.Default
    private List<RecyclingHistory> recyclingHistories = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * Add recycling history record to recycler's records
     */
    public void addRecyclingHistory(RecyclingHistory history) {
        if (this.recyclingHistories == null) {
            this.recyclingHistories = new ArrayList<>();
        }
        history.setRecycler(this);
        this.recyclingHistories.add(history);
    }

    /**
     * Remove recycling history record from recycler's records
     */
    public void removeRecyclingHistory(RecyclingHistory history) {
        if (this.recyclingHistories != null) {
            this.recyclingHistories.remove(history);
            history.setRecycler(null);
        }
    }

    /**
     * Update average rating based on new review
     */
    public void updateRating(Double newRating) {
        if (this.ratings == null) {
            this.ratings = 0.0;
        }
        if (this.totalReviews == null) {
            this.totalReviews = 0;
        }

        double totalRatingPoints = this.ratings * this.totalReviews;
        this.totalReviews++;
        this.ratings = (totalRatingPoints + newRating) / this.totalReviews;
    }

    /**
     * Check if recycler can handle additional capacity
     */
    public boolean canHandleWeight(Double weight) {
        return weight != null && weight <= this.recyclingCapacity;
    }
}

