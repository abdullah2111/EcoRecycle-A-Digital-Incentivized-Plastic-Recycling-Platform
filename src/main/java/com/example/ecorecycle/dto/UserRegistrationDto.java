package com.example.ecorecycle.dto;

import com.example.ecorecycle.entity.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * DTO for User Registration (Household/Business)
 */
@Data
public class UserRegistrationDto {
    // Base User fields
    @NotBlank(message = "Username is required")
    private String username;

    @Email(message = "Valid email is required")
    @NotBlank(message = "Email is required")
    private String email;

    @NotBlank(message = "Password is required")
    private String password;

    @NotBlank(message = "Confirm password")
    private String confirmPassword;

    // Profile fields
    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Phone is required")
    private String phone;

    private String gender; // MALE, FEMALE, OTHER - dropdown selection

    // Address fields - separated components
    @NotBlank(message = "District is required")
    private String district;

    @NotBlank(message = "Thana is required")
    private String thana;

    @NotBlank(message = "Area is required")
    private String area;

    @NotBlank(message = "Address (Road, House/Flat) is required")
    private String addressLine; // Road, House/Flat number, etc.

    // Role selection
    @NotNull(message = "User role is required")
    private Role role; // ROLE_HOUSEHOLD or ROLE_BUSINESS

    // Business-specific fields
    private String businessName;
    private String businessType;
    private String businessRegistrationNumber;
    private String taxId;
}
