package com.example.ecorecycle.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Profile for Recycler users
 * Uses @MapsId to share primary key with BaseUser
 */
@Entity
@Table(name = "recycler_profiles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecyclerProfile {

    @Id
    @Column(name = "user_id")
    private Long userId;

    @OneToOne
    @MapsId
    @JoinColumn(name = "user_id")
    private BaseUser baseUser;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "phone", nullable = false)
    private String phone;


    @Column(name = "district", nullable = false, length = 100)
    private String district;

    @Column(name = "thana", nullable = false, length = 100)
    private String thana;

    @Column(name = "area", nullable = false, length = 200)
    private String area;

    @Column(name = "address_line", nullable = false, length = 300)
    private String addressLine; // Road, House/Flat number combined

    @Column(name = "service_area", nullable = false, length = 500)
    private String serviceArea;

    @Column(name = "pickup_schedule", length = 1000)
    private String pickupSchedule;

    @Column(name = "recycling_capacity", nullable = false)
    private Double recyclingCapacity;

    @Column(name = "operating_hours")
    private String operatingHours;

    @Column(name = "accepted_plastic_types", length = 1000)
    private String acceptedPlasticTypes;

    @Column(name = "ratings")
    @Builder.Default
    private Double ratings = 0.0;

    @Column(name = "total_reviews")
    @Builder.Default
    private Integer totalReviews = 0;
}
