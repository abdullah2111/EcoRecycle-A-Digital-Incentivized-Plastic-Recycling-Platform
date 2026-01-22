package com.example.ecorecycle.service;

import com.example.ecorecycle.dto.PickupRequestDto;
import com.example.ecorecycle.dto.PickupResponseDto;

import java.util.List;

public interface PickupRequestService {
    PickupResponseDto createPickupRequest(Long userId, PickupRequestDto requestDto);
    PickupResponseDto acceptPickupRequest(Long pickupId, Long recyclerId);
    PickupResponseDto completePickupRequest(Long pickupId, Long recyclerId, Double actualWeight);
    void cancelPickupRequest(Long pickupId, Long userId);
    List<PickupResponseDto> getPickupRequestsByUser(Long userId);
    List<PickupResponseDto> getPickupRequestsByRecycler(Long recyclerId);
    List<PickupResponseDto> getPendingRequestsInArea(Long recyclerId);

}
