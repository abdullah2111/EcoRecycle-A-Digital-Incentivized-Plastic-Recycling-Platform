package com.example.ecorecycle.service;

import com.example.ecorecycle.dto.PickupRequestDto;
import com.example.ecorecycle.entity.BaseUser;
import com.example.ecorecycle.entity.PickupRequest;
import com.example.ecorecycle.entity.Role;
import com.example.ecorecycle.repository.BaseUserRepository;
import com.example.ecorecycle.repository.PickupRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PickupService {

    private final PickupRequestRepository pickupRequestRepository;
    private final BaseUserRepository baseUserRepository;

    /**
     * Create a new pickup request
     */
    @Transactional
    public PickupRequest createPickupRequest(String username, PickupRequestDto dto) {
        BaseUser user = baseUserRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Build pickup request
        PickupRequest pickupRequest = PickupRequest.builder()
                .user(user)
                .userType(user.getRole())
                .plasticTypes(String.join(",", dto.getPlasticTypes()))
                .approxWeight(dto.getApproxWeight())
                .preferredDate(dto.getPreferredDate())
                .preferredTime(dto.getPreferredTime())
                .additionalNotes(dto.getAdditionalNotes())
                .status(PickupRequest.PickupStatus.PENDING)
                .build();

        // Set user-specific information based on role
        if (user.getRole() == Role.ROLE_HOUSEHOLD && user.getHouseholdProfile() != null) {
            var profile = user.getHouseholdProfile();
            pickupRequest.setUserName(profile.getName());
            pickupRequest.setDistrict(profile.getDistrict());
            pickupRequest.setThana(profile.getThana());
            pickupRequest.setArea(profile.getArea());
            pickupRequest.setAddressLine(profile.getAddressLine());
            pickupRequest.setContactPhone(profile.getPhone());
            pickupRequest.setContactEmail(user.getEmail());
        } else if (user.getRole() == Role.ROLE_BUSINESS && user.getBusinessProfile() != null) {
            var profile = user.getBusinessProfile();
            pickupRequest.setUserName(profile.getName());
            pickupRequest.setBusinessName(profile.getName());
            pickupRequest.setDistrict(profile.getDistrict());
            pickupRequest.setThana(profile.getThana());
            pickupRequest.setArea(profile.getArea());
            pickupRequest.setAddressLine(profile.getAddressLine());
            pickupRequest.setContactPhone(profile.getPhone());
            pickupRequest.setContactEmail(user.getEmail());
        }

        return pickupRequestRepository.save(pickupRequest);
    }

    /**
     * Get all pickup requests for a user
     */
    public List<PickupRequest> getUserPickupRequests(String username) {
        BaseUser user = baseUserRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return pickupRequestRepository.findByUserOrderByCreatedAtDesc(user);
    }

    /**
     * Get pickup request by ID
     */
    public PickupRequest getPickupRequestById(Long pickupId) {
        return pickupRequestRepository.findById(pickupId)
                .orElseThrow(() -> new RuntimeException("Pickup request not found"));
    }

    /**
     * Update pickup request status
     */
    @Transactional
    public PickupRequest updatePickupStatus(Long pickupId, PickupRequest.PickupStatus newStatus) {
        PickupRequest pickupRequest = getPickupRequestById(pickupId);
        pickupRequest.setStatus(newStatus);

        // Set timestamps based on status
        if (newStatus == PickupRequest.PickupStatus.COMPLETED) {
            pickupRequest.setCompletedAt(LocalDateTime.now());
        } else if (newStatus == PickupRequest.PickupStatus.CANCELLED) {
            pickupRequest.setCancelledAt(LocalDateTime.now());
        }

        return pickupRequestRepository.save(pickupRequest);
    }

    /**
     * Cancel pickup request
     */
    @Transactional
    public PickupRequest cancelPickupRequest(Long pickupId, String reason) {
        PickupRequest pickupRequest = getPickupRequestById(pickupId);
        pickupRequest.setStatus(PickupRequest.PickupStatus.CANCELLED);
        pickupRequest.setCancelledAt(LocalDateTime.now());
        pickupRequest.setCancellationReason(reason);
        return pickupRequestRepository.save(pickupRequest);
    }

    /**
     * Get pickup requests by status
     */
    public List<PickupRequest> getPickupRequestsByStatus(PickupRequest.PickupStatus status) {
        return pickupRequestRepository.findByStatusOrderByCreatedAtDesc(status);
    }

    /**
     * Count total pickups for a user
     */
    public Long countUserPickups(String username) {
        BaseUser user = baseUserRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return pickupRequestRepository.countByUser(user);
    }

    /**
     * Get pending pickup requests in an area (for recyclers)
     */
    public List<PickupRequest> getPendingPickupsInArea(String area) {
        return pickupRequestRepository.findByStatusAndAreaOrderByCreatedAtDesc(
                PickupRequest.PickupStatus.PENDING, area);
    }

    /**
     * Get pending pickup requests for recycler based on their service area (thana)
     */
    public List<PickupRequest> getPendingPickupsForRecycler(String username) {
        BaseUser recycler = baseUserRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Recycler not found"));

        if (recycler.getRole() != Role.ROLE_RECYCLER || recycler.getRecyclerProfile() == null) {
            throw new RuntimeException("User is not a recycler");
        }

        String thana = recycler.getRecyclerProfile().getThana();
        return pickupRequestRepository.findByStatusAndThanaOrderByCreatedAtDesc(
                PickupRequest.PickupStatus.PENDING, thana);
    }

    /**
     * Accept pickup request (recycler accepts the request)
     */
    @Transactional
    public PickupRequest acceptPickupRequest(Long pickupId, String recyclerUsername) {
        PickupRequest pickupRequest = getPickupRequestById(pickupId);
        BaseUser recycler = baseUserRepository.findByUsername(recyclerUsername)
                .orElseThrow(() -> new RuntimeException("Recycler not found"));

        // Check if already accepted or completed
        if (pickupRequest.getStatus() != PickupRequest.PickupStatus.PENDING) {
            throw new RuntimeException("This pickup request is no longer available");
        }

        pickupRequest.setStatus(PickupRequest.PickupStatus.ACCEPTED);
        pickupRequest.setRecycler(recycler);
        pickupRequest.setScheduledDate(pickupRequest.getPreferredDate());

        return pickupRequestRepository.save(pickupRequest);
    }

    /**
     * Reject pickup request
     */
    @Transactional
    public PickupRequest rejectPickupRequest(Long pickupId) {
        PickupRequest pickupRequest = getPickupRequestById(pickupId);

        // Check if can be rejected
        if (pickupRequest.getStatus() != PickupRequest.PickupStatus.PENDING) {
            throw new RuntimeException("This pickup request cannot be rejected");
        }

        pickupRequest.setStatus(PickupRequest.PickupStatus.REJECTED);
        return pickupRequestRepository.save(pickupRequest);
    }
}

