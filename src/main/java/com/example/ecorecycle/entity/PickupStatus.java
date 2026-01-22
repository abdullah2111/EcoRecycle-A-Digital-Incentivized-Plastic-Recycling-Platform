package com.example.ecorecycle.entity;

import lombok.Getter;

/**
 * Enum representing the status of a pickup request.
 */
@Getter
public enum PickupStatus {
    PENDING("Pending - Awaiting assignment"),
    ASSIGNED("Assigned - Recycler assigned"),
    IN_PROGRESS("In Progress - Collection in progress"),
    COMPLETED("Completed - Successfully collected"),
    CANCELLED("Cancelled - Request cancelled");

    private final String description;

    PickupStatus(String description) {
        this.description = description;
    }
}

