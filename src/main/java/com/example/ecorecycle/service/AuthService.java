package com.example.ecorecycle.service;

import com.example.ecorecycle.dto.RecyclerRegistrationDto;
import com.example.ecorecycle.dto.UserRegistrationDto;
import com.example.ecorecycle.entity.*;
import com.example.ecorecycle.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final BaseUserRepository baseUserRepository;
    private final HouseholdProfileRepository householdProfileRepository;
    private final BusinessProfileRepository businessProfileRepository;
    private final RecyclerProfileRepository recyclerProfileRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public BaseUser registerUser(UserRegistrationDto dto) {
        // Validate password match
        if (!dto.getPassword().equals(dto.getConfirmPassword())) {
            throw new RuntimeException("Passwords do not match");
        }

        // Check if username or email already exists
        if (baseUserRepository.existsByUsername(dto.getUsername())) {
            throw new RuntimeException("Username already exists");
        }
        if (baseUserRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        // Create base user with minimal fields
        BaseUser baseUser = BaseUser.builder()
                .username(dto.getUsername())
                .email(dto.getEmail())
                .password(passwordEncoder.encode(dto.getPassword()))
                .role(dto.getRole())
                .isActive(true)
                .build();

        // Save base user first to get ID
        baseUser = baseUserRepository.save(baseUser);

        // Create appropriate profile based on role
        if (dto.getRole() == Role.ROLE_HOUSEHOLD) {
            HouseholdProfile profile = HouseholdProfile.builder()
                    .baseUser(baseUser)
                    .name(dto.getName())
                    .phone(dto.getPhone())
                    .gender(dto.getGender())
                    .district(dto.getDistrict())
                    .thana(dto.getThana())
                    .area(dto.getArea())
                    .addressLine(dto.getAddressLine())
                    .ecoPoints(0L)
                    .build();
            householdProfileRepository.save(profile);
            baseUser.setHouseholdProfile(profile);
        } else if (dto.getRole() == Role.ROLE_BUSINESS) {
            BusinessProfile profile = BusinessProfile.builder()
                    .baseUser(baseUser)
                    .name(dto.getName())
                    .phone(dto.getPhone())
                    .district(dto.getDistrict())
                    .thana(dto.getThana())
                    .area(dto.getArea())
                    .addressLine(dto.getAddressLine())
                    .businessName(dto.getBusinessName())
                    .businessType(dto.getBusinessType())
                    .businessRegistrationNumber(dto.getBusinessRegistrationNumber())
                    .taxId(dto.getTaxId())
                    .ecoPoints(0L)
                    .build();
            businessProfileRepository.save(profile);
            baseUser.setBusinessProfile(profile);
        }

        return baseUser;
    }

    @Transactional
    public BaseUser registerRecycler(RecyclerRegistrationDto dto) {
        // Validate password match
        if (!dto.getPassword().equals(dto.getConfirmPassword())) {
            throw new RuntimeException("Passwords do not match");
        }

        // Check if username or email already exists
        if (baseUserRepository.existsByUsername(dto.getUsername())) {
            throw new RuntimeException("Username already exists");
        }
        if (baseUserRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        // Create base user with minimal fields
        BaseUser baseUser = BaseUser.builder()
                .username(dto.getUsername())
                .email(dto.getEmail())
                .password(passwordEncoder.encode(dto.getPassword()))
                .role(Role.ROLE_RECYCLER)
                .isActive(true)
                .build();

        // Save base user first
        baseUser = baseUserRepository.save(baseUser);

        // Create recycler profile
        RecyclerProfile profile = RecyclerProfile.builder()
                .baseUser(baseUser)
                .name(dto.getName())
                .phone(dto.getPhone())
                .district(dto.getDistrict())
                .thana(dto.getThana())
                .area(dto.getArea())
                .addressLine(dto.getAddressLine())
                .serviceArea(dto.getServiceArea())
                .pickupSchedule(dto.getPickupSchedule())
                .recyclingCapacity(dto.getRecyclingCapacity())
                .operatingHours(dto.getOperatingHours())
                .acceptedPlasticTypes(dto.getAcceptedPlasticTypes())
                .ratings(0.0)
                .totalReviews(0)
                .build();
        recyclerProfileRepository.save(profile);
        baseUser.setRecyclerProfile(profile);

        return baseUser;
    }
}
