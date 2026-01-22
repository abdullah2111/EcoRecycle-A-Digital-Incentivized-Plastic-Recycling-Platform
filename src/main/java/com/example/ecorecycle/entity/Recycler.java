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
@ToString(exclude = {"recyclingHistories", "pickupRequests"})
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

    @Column(name = "password", nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 30)
    @Builder.Default
    private Role role = Role.RECYCLER;

    @Column(name = "service_area", nullable = false, length = 500)
    private String serviceArea;

    @Column(name = "pickup_schedule", length = 1000)
    private String pickupSchedule;

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


    @OneToMany(
            mappedBy = "recycler",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    @Builder.Default
    private List<RecyclingHistory> recyclingHistories = new ArrayList<>();

    @OneToMany(
            mappedBy = "recycler",
            cascade = {CascadeType.PERSIST, CascadeType.MERGE},
            fetch = FetchType.LAZY
    )
    @Builder.Default
    private List<PickupRequest> pickupRequests = new ArrayList<>();

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
     * Add pickup request to recycler's requests
     */
    public void addPickupRequest(PickupRequest request) {
        if (this.pickupRequests == null) {
            this.pickupRequests = new ArrayList<>();
        }
        request.setRecycler(this);
        this.pickupRequests.add(request);
    }

    /**
     * Remove pickup request from recycler's requests
     */
    public void removePickupRequest(PickupRequest request) {
        if (this.pickupRequests != null) {
            this.pickupRequests.remove(request);
            request.setRecycler(null);
        }
    }
}
