package com.example.ecorecycle.service;

import com.example.ecorecycle.dto.PickupRequestDto;
import com.example.ecorecycle.entity.*;
import com.example.ecorecycle.repository.*;
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
    private final HouseholdProfileRepository householdProfileRepository;
    private final BusinessProfileRepository businessProfileRepository;
    private final RecyclerProfileRepository recyclerProfileRepository;
    private final EcoPointsService ecoPointsService;

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
     * Get user by username
     */
    public BaseUser getUserByUsername(String username) {
        return baseUserRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    /**
     * Get pickup request by ID
     */
    public PickupRequest getPickupRequestById(Long pickupId) {
        return pickupRequestRepository.findById(pickupId)
                .orElseThrow(() -> new RuntimeException("Pickup request not found"));
    }

    /**
     * Save pickup request (for updates)
     */
    @Transactional
    public PickupRequest savePickupRequest(PickupRequest pickupRequest) {
        return pickupRequestRepository.save(pickupRequest);
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
            pickupRequest.setCompletedAt(java.time.LocalDateTime.now());
        } else if (newStatus == PickupRequest.PickupStatus.CANCELLED) {
            pickupRequest.setCancelledAt(java.time.LocalDateTime.now());
        }

        return pickupRequestRepository.save(pickupRequest);
    }

    /**
     * Cancel pickup request by user
     */
    @Transactional
    public PickupRequest cancelPickupRequest(Long pickupId, String reason) {
        PickupRequest pickupRequest = getPickupRequestById(pickupId);
        pickupRequest.setStatus(PickupRequest.PickupStatus.CANCELLED);
        pickupRequest.setCancelledAt(java.time.LocalDateTime.now());
        pickupRequest.setCancellationReason(reason);
        pickupRequest.setCancelledBy("USER");
        return pickupRequestRepository.save(pickupRequest);
    }

    /**
     * Cancel pickup request by recycler
     */
    @Transactional
    public PickupRequest cancelPickupRequestByRecycler(Long pickupId, String reason) {
        PickupRequest pickupRequest = getPickupRequestById(pickupId);
        pickupRequest.setStatus(PickupRequest.PickupStatus.CANCELLED);
        pickupRequest.setCancelledAt(java.time.LocalDateTime.now());
        pickupRequest.setCancellationReason(reason);
        pickupRequest.setCancelledBy("RECYCLER");
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

    /**
     * Get assigned pickups for recycler in processing pipeline
     */
    public List<PickupRequest> getAssignedPickupsForRecycler(String username) {
        BaseUser recycler = baseUserRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Recycler not found"));
        if (recycler.getRole() != Role.ROLE_RECYCLER) {
            throw new RuntimeException("User is not a recycler");
        }
        List<PickupRequest.PickupStatus> statuses = List.of(
                PickupRequest.PickupStatus.ACCEPTED,
                PickupRequest.PickupStatus.SCHEDULED,
                PickupRequest.PickupStatus.IN_PROGRESS,
                PickupRequest.PickupStatus.DELAYED
        );
        return pickupRequestRepository.findByRecyclerAndStatusInOrderByCreatedAtDesc(recycler, statuses);
    }

    /**
     * Update status for recycler-assigned pickup
     */
    @Transactional
    public PickupRequest updateRecyclerPickupStatus(Long pickupId, String recyclerUsername, PickupRequest.PickupStatus newStatus) {
        PickupRequest pickupRequest = getPickupRequestById(pickupId);
        BaseUser recycler = baseUserRepository.findByUsername(recyclerUsername)
                .orElseThrow(() -> new RuntimeException("Recycler not found"));

        if (pickupRequest.getRecycler() == null || !pickupRequest.getRecycler().getUserId().equals(recycler.getUserId())) {
            throw new RuntimeException("You are not assigned to this pickup");
        }

        // Allow cancellation at any time except when already completed or cancelled
        if (newStatus == PickupRequest.PickupStatus.CANCELLED) {
            if (pickupRequest.getStatus() == PickupRequest.PickupStatus.COMPLETED) {
                throw new RuntimeException("Cannot cancel a completed pickup");
            }
            if (pickupRequest.getStatus() == PickupRequest.PickupStatus.CANCELLED) {
                throw new RuntimeException("Pickup already cancelled");
            }
        } else {
            // Valid transitions for status progression
            if (pickupRequest.getStatus() == PickupRequest.PickupStatus.ACCEPTED &&
                    (newStatus == PickupRequest.PickupStatus.SCHEDULED || newStatus == PickupRequest.PickupStatus.IN_PROGRESS || newStatus == PickupRequest.PickupStatus.DELAYED)) {
                // allow
            } else if (pickupRequest.getStatus() == PickupRequest.PickupStatus.SCHEDULED &&
                    (newStatus == PickupRequest.PickupStatus.IN_PROGRESS || newStatus == PickupRequest.PickupStatus.DELAYED)) {
                // allow
            } else if (pickupRequest.getStatus() == PickupRequest.PickupStatus.IN_PROGRESS &&
                    (newStatus == PickupRequest.PickupStatus.COMPLETED || newStatus == PickupRequest.PickupStatus.DELAYED)) {
                // allow
            } else if (pickupRequest.getStatus() == PickupRequest.PickupStatus.DELAYED &&
                    (newStatus == PickupRequest.PickupStatus.IN_PROGRESS || newStatus == PickupRequest.PickupStatus.COMPLETED)) {
                // allow
            } else if (pickupRequest.getStatus() == PickupRequest.PickupStatus.COMPLETED) {
                throw new RuntimeException("Pickup already completed");
            } else {
                throw new RuntimeException("Invalid status transition");
            }
        }

        pickupRequest.setStatus(newStatus);
        if (newStatus == PickupRequest.PickupStatus.COMPLETED) {
            pickupRequest.setCompletedAt(java.time.LocalDateTime.now());

            // Calculate eco points and award to the user's profile (household or business)
            Long ecoPoints = ecoPointsService.calculateEcoPoints(
                    pickupRequest.getPlasticTypes(),
                    pickupRequest.getApproxWeight()
            );

            // Store eco points on pickup request for display in timeline
            pickupRequest.setEcoPointsAwarded(ecoPoints);

            BaseUser pickupUser = pickupRequest.getUser();
            if (pickupUser.getRole() == Role.ROLE_HOUSEHOLD) {
                householdProfileRepository.findById(pickupUser.getUserId())
                        .ifPresent(profile -> {
                            if (profile.getEcoPoints() == null) profile.setEcoPoints(0L);
                            profile.setEcoPoints(profile.getEcoPoints() + ecoPoints);
                            householdProfileRepository.save(profile);
                        });
            } else if (pickupUser.getRole() == Role.ROLE_BUSINESS) {
                businessProfileRepository.findById(pickupUser.getUserId())
                        .ifPresent(profile -> {
                            if (profile.getEcoPoints() == null) profile.setEcoPoints(0L);
                            profile.setEcoPoints(profile.getEcoPoints() + ecoPoints);
                            businessProfileRepository.save(profile);
                        });
            }
        } else if (newStatus == PickupRequest.PickupStatus.CANCELLED) {
            pickupRequest.setCancelledAt(java.time.LocalDateTime.now());
        }
        return pickupRequestRepository.save(pickupRequest);
    }

    /**
     * Get completed and cancelled pickups for recycler (order history)
     */
    public List<PickupRequest> getCompletedAndCancelledPickupsForRecycler(String username) {
        BaseUser recycler = baseUserRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Recycler not found"));
        if (recycler.getRole() != Role.ROLE_RECYCLER) {
            throw new RuntimeException("User is not a recycler");
        }
        List<PickupRequest.PickupStatus> statuses = List.of(
                PickupRequest.PickupStatus.COMPLETED,
                PickupRequest.PickupStatus.CANCELLED
        );
        return pickupRequestRepository.findByRecyclerAndStatusInOrderByCreatedAtDesc(recycler, statuses);
    }

    /**
     * Submit review for completed pickup
     */
    @Transactional
    public PickupRequest submitReview(Long pickupId, Integer rating, String comment) {
        PickupRequest pickupRequest = getPickupRequestById(pickupId);

        if (pickupRequest.getStatus() != PickupRequest.PickupStatus.COMPLETED) {
            throw new RuntimeException("Can only review completed pickups");
        }

        // Prevent duplicate reviews: if already reviewed, block
        if (pickupRequest.getReviewedAt() != null) {
            throw new RuntimeException("Pickup already reviewed");
        }

        if (rating == null || rating < 1 || rating > 5) {
            throw new RuntimeException("Rating must be between 1 and 5");
        }

        // Update recycler's aggregated rating and store last review/comment
        BaseUser recyclerUser = pickupRequest.getRecycler();
        if (recyclerUser == null || recyclerUser.getRecyclerProfile() == null) {
            throw new RuntimeException("Recycler profile not found for this order");
        }

        RecyclerProfile profile = recyclerUser.getRecyclerProfile();
        double currentAvg = profile.getRatings() != null ? profile.getRatings() : 0.0;
        int currentCount = profile.getTotalReviews() != null ? profile.getTotalReviews() : 0;
        double newAvg = ((currentAvg * currentCount) + rating) / (currentCount + 1);
        profile.setRatings(newAvg);
        profile.setTotalReviews(currentCount + 1);
        profile.setLastReviewComment(comment);
        profile.setLastReviewedAt(LocalDateTime.now());
        recyclerProfileRepository.save(profile);

        // Mark pickup as reviewed (do not store rating/comment on pickup per requirement)
        pickupRequest.setReviewedAt(LocalDateTime.now());

        return pickupRequestRepository.save(pickupRequest);
    }

    /**
     * Acknowledge completed order (mark as seen by user)
     */
    @Transactional
    public PickupRequest acknowledgeOrder(Long pickupId) {
        PickupRequest pickupRequest = getPickupRequestById(pickupId);

        if (pickupRequest.getStatus() != PickupRequest.PickupStatus.COMPLETED) {
            throw new RuntimeException("Can only acknowledge completed orders");
        }

        pickupRequest.setAcknowledged(true);
        return pickupRequestRepository.save(pickupRequest);
    }

    /**
     * Ensure eco points are awarded for completed pickups (backfill helper)
     */
    @Transactional
    public PickupRequest ensureEcoPointsAwarded(PickupRequest pickupRequest) {
        if (pickupRequest.getStatus() == PickupRequest.PickupStatus.COMPLETED) {
            Long currentPoints = pickupRequest.getEcoPointsAwarded();
            if (currentPoints == null || currentPoints == 0L) {
                Long ecoPoints = ecoPointsService.calculateEcoPoints(
                        pickupRequest.getPlasticTypes(),
                        pickupRequest.getApproxWeight()
                );
                pickupRequest.setEcoPointsAwarded(ecoPoints);
                return pickupRequestRepository.save(pickupRequest);
            }
        }
        return pickupRequest;
    }
}
