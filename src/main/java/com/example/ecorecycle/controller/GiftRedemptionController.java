package com.example.ecorecycle.controller;

import com.example.ecorecycle.entity.EcoGift;
import com.example.ecorecycle.entity.GiftRedemption;
import com.example.ecorecycle.service.GiftRedemptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

/**
 * Controller for gift redemption features
 */
@Controller
@RequestMapping("/{username}/redeem")
@RequiredArgsConstructor
public class GiftRedemptionController {

    private final GiftRedemptionService giftRedemptionService;

    /**
     * View redeem gifts page with available gifts and user's eco points
     */
    @GetMapping("/gifts")
    public String viewRedeemGifts(@PathVariable String username,
                                  Authentication auth,
                                  Model model) {
        if (auth == null || !auth.getName().equals(username)) {
            return "redirect:/login";
        }

        try {
            // Get available gifts
            List<EcoGift> gifts = giftRedemptionService.getAvailableGifts();

            // Get user's eco points
            Long userEcoPoints = giftRedemptionService.getUserEcoPoints(username);

            model.addAttribute("gifts", gifts);
            model.addAttribute("userEcoPoints", userEcoPoints);
            model.addAttribute("username", username);
            return "user/redeem/redeem-gifts";
        } catch (Exception e) {
            try {
                return "redirect:/" + username + "/dashboard?error=" + URLEncoder.encode("Error loading gifts: " + e.getMessage(), "UTF-8");
            } catch (UnsupportedEncodingException ex) {
                return "redirect:/" + username + "/dashboard";
            }
        }
    }

    /**
     * Redeem a gift (POST)
     */
    @PostMapping("/order/{giftId}")
    public String redeemGift(@PathVariable String username,
                             @PathVariable Long giftId,
                             @RequestParam(defaultValue = "1") Integer quantity,
                             @RequestParam String deliveryAddress,
                             Authentication auth) {
        if (auth == null || !auth.getName().equals(username)) {
            return "redirect:/login";
        }

        try {
            GiftRedemption redemption = giftRedemptionService.redeemGift(username, giftId, quantity, deliveryAddress);

            return "redirect:/" + username + "/redeem/gifts?success=" + URLEncoder.encode(
                    "Gift ordered successfully! Order ID: #" + redemption.getRedemptionId(), "UTF-8");
        } catch (RuntimeException e) {
            try {
                return "redirect:/" + username + "/redeem/gifts?error=" + URLEncoder.encode(e.getMessage(), "UTF-8");
            } catch (UnsupportedEncodingException ex) {
                return "redirect:/" + username + "/redeem/gifts";
            }
        } catch (Exception e) {
            try {
                return "redirect:/" + username + "/redeem/gifts?error=" + URLEncoder.encode("Error processing order", "UTF-8");
            } catch (UnsupportedEncodingException ex) {
                return "redirect:/" + username + "/redeem/gifts";
            }
        }
    }

    /**
     * View redemption history
     */
    @GetMapping("/history")
    public String viewRedemptionHistory(@PathVariable String username,
                                        Authentication auth,
                                        Model model) {
        if (auth == null || !auth.getName().equals(username)) {
            return "redirect:/login";
        }

        try {
            List<GiftRedemption> redemptions = giftRedemptionService.getUserRedemptionHistory(username);
            Long userEcoPoints = giftRedemptionService.getUserEcoPoints(username);

            model.addAttribute("redemptions", redemptions);
            model.addAttribute("userEcoPoints", userEcoPoints);
            model.addAttribute("username", username);
            return "user/redeem/redemption-history";
        } catch (Exception e) {
            try {
                return "redirect:/" + username + "/dashboard?error=" + URLEncoder.encode("Error loading history", "UTF-8");
            } catch (UnsupportedEncodingException ex) {
                return "redirect:/" + username + "/dashboard";
            }
        }
    }

    /**
     * Cancel a redemption
     */
    @PostMapping("/cancel/{redemptionId}")
    public String cancelRedemption(@PathVariable String username,
                                   @PathVariable Long redemptionId,
                                   Authentication auth) {
        if (auth == null || !auth.getName().equals(username)) {
            return "redirect:/login";
        }

        try {
            giftRedemptionService.cancelRedemption(redemptionId, username);

            return "redirect:/" + username + "/redeem/history?success=" + URLEncoder.encode(
                    "Order cancelled successfully! Eco points refunded.", "UTF-8");
        } catch (RuntimeException e) {
            try {
                return "redirect:/" + username + "/redeem/history?error=" + URLEncoder.encode(e.getMessage(), "UTF-8");
            } catch (UnsupportedEncodingException ex) {
                return "redirect:/" + username + "/redeem/history";
            }
        } catch (Exception e) {
            try {
                return "redirect:/" + username + "/redeem/history?error=" + URLEncoder.encode("Error cancelling order", "UTF-8");
            } catch (UnsupportedEncodingException ex) {
                return "redirect:/" + username + "/redeem/history";
            }
        }
    }
}

