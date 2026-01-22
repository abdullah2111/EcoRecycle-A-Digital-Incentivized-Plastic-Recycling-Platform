package com.example.ecorecycle.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * PickupRequest entity representing a waste collection request.
 * Links a user's pickup request with an assigned recycler.
 */
@Entity
@Table(
    name = "pickup_requests",
    indexes = {
        @Index(name = "idx_user_id", columnList = "user_id"),
        @Index(name = "idx_recycler_id", columnList = "recycler_id"),
        @Index(name = "idx_pickup_date", columnList = "pickup_date"),
        @Index(name = "idx_status", columnList = "status")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"user", "recycler"})
@EqualsAndHashCode(exclude = {"user", "recycler"})
public class PickupRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pickup_id")
    private Long pickupId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recycler_id")
    private Recycler recycler;

    @Column(name = "pickup_address", nullable = false, length = 500)
    private String pickupAddress;

    @Column(name = "pickup_date", nullable = false)
    private LocalDateTime pickupDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private PickupStatus status = PickupStatus.PENDING;

    @Column(name = "estimated_weight")
    private Double estimatedWeight;

    @Column(name = "plastic_type", length = 255)
    private String plasticType;

    @Column(name = "notes", length = 1000)
    private String notes;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * Mark pickup request as completed
     */
    public void complete() {
        this.status = PickupStatus.COMPLETED;
    }

    /**
     * Mark pickup request as cancelled
     */
    public void cancel() {
        this.status = PickupStatus.CANCELLED;
    }

    /**
     * Assign a recycler to this pickup request
     */
    public void assignRecycler(Recycler recycler) {
        this.recycler = recycler;
        if (this.status == PickupStatus.PENDING) {
            this.status = PickupStatus.ASSIGNED;
        }
    }

    /**
     * Check if pickup request is still modifiable
     */
    public boolean isModifiable() {
        return this.status == PickupStatus.PENDING || this.status == PickupStatus.ASSIGNED;
    }
}

