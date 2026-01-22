package com.example.ecorecycle.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * DTO for Recycler Registration
 */
@Data
public class RecyclerRegistrationDto {
    // Base user fields
    @NotBlank(message = "Username is required")
    private String username;

    @Email(message = "Valid email is required")
    @NotBlank(message = "Email is required")
    private String email;

    @NotBlank(message = "Password is required")
    private String password;

    @NotBlank(message = "Confirm password")
    private String confirmPassword;

    // Recycler profile fields
    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Phone is required")
    private String phone;

    // Address fields - separated components
    @NotBlank(message = "District is required")
    private String district;

    @NotBlank(message = "Thana is required")
    private String thana;

    @NotBlank(message = "Area is required")
    private String area;

    @NotBlank(message = "Address (Road, House/Flat) is required")
    private String addressLine; // Road, House/Flat number, etc.

    @NotBlank(message = "Service area is required")
    private String serviceArea;

    @NotBlank(message = "Pickup schedule is required")
    private String pickupSchedule;

    @NotNull(message = "Recycling capacity is required")
    @Min(value = 1, message = "Capacity must be at least 1 kg")
    private Double recyclingCapacity;

    @NotBlank(message = "Operating hours is required")
    private String operatingHours;

    @NotBlank(message = "Accepted plastic types is required")
    private String acceptedPlasticTypes;
}
