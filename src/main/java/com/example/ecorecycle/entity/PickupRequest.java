package com.example.ecorecycle.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * PickupRequest entity to store waste collection requests from users
 */
@Entity
@Table(name = "pickup_requests",
        indexes = {
                @Index(name = "idx_user_id", columnList = "user_id"),
                @Index(name = "idx_status", columnList = "status"),
                @Index(name = "idx_preferred_date", columnList = "preferred_date")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PickupRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pickup_id")
    private Long pickupId;

    // User relationship (Many-to-One)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private BaseUser user;

    @Column(name = "user_name", nullable = false)
    private String userName;

    @Column(name = "business_name")
    private String businessName;

    @Column(name = "user_type", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private Role userType;

    // Address Information
    @Column(name = "district", nullable = false)
    private String district;

    @Column(name = "thana", nullable = false)
    private String thana;

    @Column(name = "area", nullable = false)
    private String area;

    @Column(name = "address_line", nullable = false)
    private String addressLine;

    // Pickup Details
    @Column(name = "plastic_types", nullable = false, length = 500)
    private String plasticTypes; // Stored as comma-separated values

    @Column(name = "approx_weight", nullable = false)
    private Double approxWeight;

    @Column(name = "preferred_date", nullable = false)
    private LocalDate preferredDate;

    @Column(name = "preferred_time", nullable = false)
    private LocalTime preferredTime;

    @Column(name = "additional_notes", columnDefinition = "TEXT")
    private String additionalNotes;

    // Status
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private PickupStatus status = PickupStatus.PENDING;

    // Recycler assignment (optional - can be assigned later)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recycler_id")
    private BaseUser recycler;

    // Timestamps
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "scheduled_date")
    private LocalDate scheduledDate;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt;

    @Column(name = "cancellation_reason", columnDefinition = "TEXT")
    private String cancellationReason;

    @Column(name = "cancelled_by", length = 20)
    private String cancelledBy; // USER or RECYCLER

    // Contact Information
    @Column(name = "contact_phone", nullable = false)
    private String contactPhone;

    @Column(name = "contact_email")
    private String contactEmail;

    @Column(name = "eco_points_awarded")
    @Builder.Default
    private Long ecoPointsAwarded = 0L;

    // Review fields
    @Column(name = "rating")
    private Integer rating; // 1-5 star rating

    @Column(name = "review_comment", columnDefinition = "TEXT")
    private String reviewComment;

    @Column(name = "reviewed_at")
    private LocalDateTime reviewedAt;

    @Column(name = "acknowledged")
    @Builder.Default
    private Boolean acknowledged = false; // User acknowledged completed order

    /**
     * Enum for Pickup Request Status
     */
    public enum PickupStatus {
        PENDING,      // Initial status when request is created
        ACCEPTED,     // Recycler has accepted the request
        SCHEDULED,    // Pickup has been scheduled
        IN_PROGRESS,  // Pickup is in progress
        COMPLETED,    // Pickup completed successfully
        CANCELLED,    // User cancelled the request
        REJECTED,     // Recycler rejected the request
        DELAYED       // Pickup is delayed
    }
}

