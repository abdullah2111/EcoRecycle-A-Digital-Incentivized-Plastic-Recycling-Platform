package com.example.ecorecycle.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * DTO for creating a new pickup request
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PickupRequestDto {

    private List<String> plasticTypes;
    private Double approxWeight;
    private LocalDate preferredDate;
    private LocalTime preferredTime;
    private String additionalNotes;
}

