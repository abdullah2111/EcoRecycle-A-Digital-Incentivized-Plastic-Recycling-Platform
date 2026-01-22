package com.example.ecorecycle.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PickupRequestDto {
    private String pickupAddress;
    private LocalDateTime pickupDate;
    private Double estimatedWeight;
    private String plasticType;
    private String notes;
}