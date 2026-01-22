package com.example.ecorecycle.dto;

import com.example.ecorecycle.entity.PickupStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PickupResponseDto {
    private Long pickupId;
    private Long userId;
    private Long recyclerId;
    private String pickupAddress;
    private LocalDateTime pickupDate;
    private PickupStatus status;
    private Double estimatedWeight;
    private String plasticType;
    private LocalDateTime createdAt;
}