package com.example.ecorecycle.entity;

import lombok.Getter;

/**
 * Enum representing the type of user on the platform.
 */
@Getter
public enum UserType {
    HOUSEHOLD("Household User"),
    BUSINESS("Business User");

    private final String description;

    UserType(String description) {
        this.description = description;
    }
}

