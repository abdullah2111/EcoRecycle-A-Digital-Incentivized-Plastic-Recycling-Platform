package com.example.ecorecycle.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * User entity representing a user in the EcoRecycle platform.
 * Supports both household and business users with eco points tracking
 * and recycling history management.
 */
@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"recyclingHistory", "pickupRequests"})
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 30)
    @Builder.Default
    private Role role = Role.USER;

    @Column(name = "phone")
    private String phone;

    @Column(name = "address", length = 500)
    private String address;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_type", nullable = false)
    private UserType userType;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "eco_points", nullable = false)
    @Builder.Default
    private Long ecoPoints = 0L;

    @OneToMany(
        mappedBy = "user",
        cascade = CascadeType.ALL,
        orphanRemoval = true,
        fetch = FetchType.LAZY
    )
    @Builder.Default
    private List<RecyclingHistory> recyclingHistory = new ArrayList<>();

    @OneToMany(
        mappedBy = "user",
        cascade = CascadeType.ALL,
        orphanRemoval = true,
        fetch = FetchType.LAZY
    )
    @Builder.Default
    private List<PickupRequest> pickupRequests = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "date_joined", nullable = false, updatable = false)
    private LocalDateTime dateJoined;

    @UpdateTimestamp
    @Column(name = "date_updated")
    private LocalDateTime dateUpdated;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    /**
     * Add recycling history record to user's history
     */
    public void addRecyclingHistory(RecyclingHistory history) {
        if (this.recyclingHistory == null) {
            this.recyclingHistory = new ArrayList<>();
        }
        history.setUser(this);
        this.recyclingHistory.add(history);
    }

    /**
     * Remove recycling history record from user's history
     */
    public void removeRecyclingHistory(RecyclingHistory history) {
        if (this.recyclingHistory != null) {
            this.recyclingHistory.remove(history);
            history.setUser(null);
        }
    }

    /**
     * Add eco points to user's total
     */
    public void addEcoPoints(Long points) {
        if (this.ecoPoints == null) {
            this.ecoPoints = 0L;
        }
        this.ecoPoints += points;
    }

    /**
     * Deduct eco points from user's total
     */
    public void deductEcoPoints(Long points) {
        if (this.ecoPoints == null) {
            this.ecoPoints = 0L;
        }
        if (this.ecoPoints >= points) {
            this.ecoPoints -= points;
        }
    }

    /**
     * Add pickup request to user's requests
     */
    public void addPickupRequest(PickupRequest request) {
        if (this.pickupRequests == null) {
            this.pickupRequests = new ArrayList<>();
        }
        request.setUser(this);
        this.pickupRequests.add(request);
    }

    /**
     * Remove pickup request from user's requests
     */
    public void removePickupRequest(PickupRequest request) {
        if (this.pickupRequests != null) {
            this.pickupRequests.remove(request);
            request.setUser(null);
        }
    }
}
