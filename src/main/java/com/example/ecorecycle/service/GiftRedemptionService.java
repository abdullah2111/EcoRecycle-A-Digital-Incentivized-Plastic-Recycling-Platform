package com.example.ecorecycle.service;

import com.example.ecorecycle.entity.*;
import com.example.ecorecycle.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GiftRedemptionService {

    private final EcoGiftRepository ecoGiftRepository;
    private final GiftRedemptionRepository giftRedemptionRepository;
    private final BaseUserRepository baseUserRepository;
    private final HouseholdProfileRepository householdProfileRepository;
    private final BusinessProfileRepository businessProfileRepository;

    /**
     * Get all available eco gifts
     */
    public List<EcoGift> getAvailableGifts() {
        return ecoGiftRepository.findByIsAvailableOrderByCreatedAtDesc(true);
    }

    /**
     * Get gifts by category
     */
    public List<EcoGift> getGiftsByCategory(String category) {
        return ecoGiftRepository.findByCategoryOrderByCreatedAtDesc(category);
    }

    /**
     * Get single gift
     */
    public EcoGift getGiftById(Long giftId) {
        return ecoGiftRepository.findById(giftId)
                .orElseThrow(() -> new RuntimeException("Gift not found"));
    }

    /**
     * Get user's available eco points
     */
    public Long getUserEcoPoints(String username) {
        BaseUser user = baseUserRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.getRole() == Role.ROLE_HOUSEHOLD && user.getHouseholdProfile() != null) {
            return user.getHouseholdProfile().getEcoPoints() != null ?
                   user.getHouseholdProfile().getEcoPoints() : 0L;
        } else if (user.getRole() == Role.ROLE_BUSINESS && user.getBusinessProfile() != null) {
            return user.getBusinessProfile().getEcoPoints() != null ?
                   user.getBusinessProfile().getEcoPoints() : 0L;
        }
        return 0L;
    }

    /**
     * Redeem a gift (order gift using eco points)
     */
    @Transactional
    public GiftRedemption redeemGift(String username, Long giftId, Integer quantity, String deliveryAddress) {
        // Get user and verify
        BaseUser user = baseUserRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Get gift and verify
        EcoGift gift = getGiftById(giftId);

        if (!gift.getIsAvailable()) {
            throw new RuntimeException("This gift is no longer available");
        }

        if (gift.getStockQuantity() != null && gift.getStockQuantity() < quantity) {
            throw new RuntimeException("Insufficient stock. Available: " + gift.getStockQuantity());
        }

        // Calculate total eco points needed
        Long totalPointsNeeded = gift.getEcoPointPrice() * quantity;

        // Get user's current eco points
        Long userEcoPoints = getUserEcoPoints(username);

        if (userEcoPoints < totalPointsNeeded) {
            throw new RuntimeException("Insufficient eco points. Available: " + userEcoPoints +
                                     ", Required: " + totalPointsNeeded);
        }

        // Deduct eco points from user profile
        if (user.getRole() == Role.ROLE_HOUSEHOLD && user.getHouseholdProfile() != null) {
            HouseholdProfile profile = user.getHouseholdProfile();
            if (profile.getEcoPoints() == null) profile.setEcoPoints(0L);
            profile.setEcoPoints(profile.getEcoPoints() - totalPointsNeeded);
            householdProfileRepository.save(profile);
        } else if (user.getRole() == Role.ROLE_BUSINESS && user.getBusinessProfile() != null) {
            BusinessProfile profile = user.getBusinessProfile();
            if (profile.getEcoPoints() == null) profile.setEcoPoints(0L);
            profile.setEcoPoints(profile.getEcoPoints() - totalPointsNeeded);
            businessProfileRepository.save(profile);
        } else {
            throw new RuntimeException("User profile not found");
        }

        // Update gift stock if applicable
        if (gift.getStockQuantity() != null) {
            gift.setStockQuantity(gift.getStockQuantity() - quantity);
            ecoGiftRepository.save(gift);
        }

        // Create redemption record
        GiftRedemption redemption = GiftRedemption.builder()
                .user(user)
                .gift(gift)
                .ecoPointsSpent(totalPointsNeeded)
                .quantity(quantity)
                .status(GiftRedemption.RedemptionStatus.ORDERED)
                .deliveryAddress(deliveryAddress)
                .createdAt(LocalDateTime.now())
                .build();

        return giftRedemptionRepository.save(redemption);
    }

    /**
     * Get user's redemption history
     */
    public List<GiftRedemption> getUserRedemptionHistory(String username) {
        BaseUser user = baseUserRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return giftRedemptionRepository.findByUserOrderByCreatedAtDesc(user);
    }

    /**
     * Get user's active redemptions (non-delivered)
     */
    public List<GiftRedemption> getUserActiveRedemptions(String username) {
        BaseUser user = baseUserRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return giftRedemptionRepository.findByUserAndStatusOrderByCreatedAtDesc(user,
                GiftRedemption.RedemptionStatus.ORDERED);
    }

    /**
     * Cancel a redemption
     */
    @Transactional
    public GiftRedemption cancelRedemption(Long redemptionId, String username) {
        GiftRedemption redemption = giftRedemptionRepository.findById(redemptionId)
                .orElseThrow(() -> new RuntimeException("Redemption not found"));

        // Verify ownership
        if (!redemption.getUser().getUsername().equals(username)) {
            throw new RuntimeException("Unauthorized access");
        }

        // Only allow cancellation if not shipped
        if (redemption.getStatus() == GiftRedemption.RedemptionStatus.SHIPPED ||
            redemption.getStatus() == GiftRedemption.RedemptionStatus.DELIVERED) {
            throw new RuntimeException("Cannot cancel: Order has already been shipped");
        }

        // Refund eco points
        BaseUser user = redemption.getUser();
        if (user.getRole() == Role.ROLE_HOUSEHOLD && user.getHouseholdProfile() != null) {
            HouseholdProfile profile = user.getHouseholdProfile();
            if (profile.getEcoPoints() == null) profile.setEcoPoints(0L);
            profile.setEcoPoints(profile.getEcoPoints() + redemption.getEcoPointsSpent());
            householdProfileRepository.save(profile);
        } else if (user.getRole() == Role.ROLE_BUSINESS && user.getBusinessProfile() != null) {
            BusinessProfile profile = user.getBusinessProfile();
            if (profile.getEcoPoints() == null) profile.setEcoPoints(0L);
            profile.setEcoPoints(profile.getEcoPoints() + redemption.getEcoPointsSpent());
            businessProfileRepository.save(profile);
        }

        // Restore gift stock
        EcoGift gift = redemption.getGift();
        if (gift.getStockQuantity() != null) {
            gift.setStockQuantity(gift.getStockQuantity() + redemption.getQuantity());
            ecoGiftRepository.save(gift);
        }

        // Update redemption status
        redemption.setStatus(GiftRedemption.RedemptionStatus.CANCELLED);
        return giftRedemptionRepository.save(redemption);
    }

    /**
     * Update redemption status (admin/system)
     */
    @Transactional
    public GiftRedemption updateRedemptionStatus(Long redemptionId, GiftRedemption.RedemptionStatus newStatus) {
        GiftRedemption redemption = giftRedemptionRepository.findById(redemptionId)
                .orElseThrow(() -> new RuntimeException("Redemption not found"));

        redemption.setStatus(newStatus);
        if (newStatus == GiftRedemption.RedemptionStatus.DELIVERED) {
            redemption.setCompletedAt(LocalDateTime.now());
        }

        return giftRedemptionRepository.save(redemption);
    }

    /**
     * Create eco gift (admin function)
     */
    @Transactional
    public EcoGift createEcoGift(String name, String description, Long ecoPointPrice,
                                 String category, Integer stockQuantity, String imageUrl) {
        EcoGift gift = EcoGift.builder()
                .name(name)
                .description(description)
                .ecoPointPrice(ecoPointPrice)
                .category(category)
                .stockQuantity(stockQuantity)
                .imageUrl(imageUrl)
                .isAvailable(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        return ecoGiftRepository.save(gift);
    }
}

