package com.example.ecorecycle.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Profile for Business users
 * Uses @MapsId to share primary key with BaseUser
 */
@Entity
@Table(name = "business_profiles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BusinessProfile {

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

    @Column(name = "eco_points", nullable = false)
    @Builder.Default
    private Long ecoPoints = 0L;

    @Column(name = "business_name", nullable = false)
    private String businessName;

    @Column(name = "business_type")
    private String businessType;

    @Column(name = "business_registration_number")
    private String businessRegistrationNumber;

    @Column(name = "tax_id")
    private String taxId;
}
