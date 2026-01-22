package com.example.ecorecycle.service.impl;

import com.example.ecorecycle.dto.PickupRequestDto;
import com.example.ecorecycle.dto.PickupResponseDto;
import com.example.ecorecycle.service.PickupRequestService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PickupRequestServiceImpl implements PickupRequestService {

    @Override
    public PickupResponseDto createPickupRequest(Long userId, PickupRequestDto requestDto) {
        return null;
    }

    @Override
    public PickupResponseDto acceptPickupRequest(Long pickupId, Long recyclerId) {
        return null;
    }

    @Override
    public PickupResponseDto completePickupRequest(Long pickupId, Long recyclerId, Double actualWeight) {
        return null;
    }

    @Override
    public void cancelPickupRequest(Long pickupId, Long userId) {

    }

    @Override
    public List<PickupResponseDto> getPickupRequestsByUser(Long userId) {
        return List.of();
    }

    @Override
    public List<PickupResponseDto> getPickupRequestsByRecycler(Long recyclerId) {
        return List.of();
    }

    @Override
    public List<PickupResponseDto> getPendingRequestsInArea(Long recyclerId) {
        return List.of();
    }
}
