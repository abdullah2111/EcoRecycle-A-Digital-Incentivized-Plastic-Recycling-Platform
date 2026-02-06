package com.example.ecorecycle.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Base User entity for authentication only.
 * Contains minimal authentication fields.
 * Profile details are in separate entities using @MapsId
 */
@Entity
@Table(name = "users",
    indexes = {
        @Index(name = "idx_username", columnList = "username", unique = true),
        @Index(name = "idx_email", columnList = "email", unique = true)
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BaseUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 30)
    private Role role;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;


    @CreationTimestamp
    @Column(name = "date_joined", nullable = false, updatable = false)
    private LocalDateTime dateJoined;

    @UpdateTimestamp
    @Column(name = "date_updated")
    private LocalDateTime dateUpdated;

    // One-to-One relationships with profiles using @MapsId
    @OneToOne(mappedBy = "baseUser", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private HouseholdProfile householdProfile;

    @OneToOne(mappedBy = "baseUser", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private BusinessProfile businessProfile;

    @OneToOne(mappedBy = "baseUser", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private RecyclerProfile recyclerProfile;

    // One-to-Many relationship with PickupRequest
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<PickupRequest> pickupRequests = new ArrayList<>();

    // One-to-Many relationship for recycler's assigned pickups
    @OneToMany(mappedBy = "recycler", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<PickupRequest> assignedPickups = new ArrayList<>();

    // Helper methods to get profile-specific data
    public String getName() {
        if (role == Role.ROLE_HOUSEHOLD && householdProfile != null) {
            return householdProfile.getName();
        } else if (role == Role.ROLE_BUSINESS && businessProfile != null) {
            return businessProfile.getName();
        } else if (role == Role.ROLE_RECYCLER && recyclerProfile != null) {
            return recyclerProfile.getName();
        }
        return username;
    }
}


