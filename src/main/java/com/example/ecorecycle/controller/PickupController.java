package com.example.ecorecycle.controller;

import com.example.ecorecycle.dto.PickupRequestDto;
import com.example.ecorecycle.entity.PickupRequest;
import com.example.ecorecycle.service.PickupService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * Controller to handle all pickup request operations
 */
@Controller
@RequestMapping("/{username}/pickup")
@RequiredArgsConstructor
public class PickupController {

    private final PickupService pickupService;

    /**
     * Create a new pickup request (POST)
     */
    @PostMapping("/request")
    public String createPickupRequest(@PathVariable String username,
                                      Authentication auth,
                                      @RequestParam List<String> plasticTypes,
                                      @RequestParam Double approxWeight,
                                      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate preferredDate,
                                      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime preferredTime,
                                      @RequestParam(required = false) String additionalNotes,
                                      RedirectAttributes redirectAttributes) {

        // Security check
        if (auth == null || !auth.getName().equals(username)) {
            return "redirect:/login";
        }

        try {
            // Create DTO
            PickupRequestDto dto = PickupRequestDto.builder()
                    .plasticTypes(plasticTypes)
                    .approxWeight(approxWeight)
                    .preferredDate(preferredDate)
                    .preferredTime(preferredTime)
                    .additionalNotes(additionalNotes)
                    .build();

            // Create pickup request
            PickupRequest pickupRequest = pickupService.createPickupRequest(username, dto);

            // Success message
            String successMessage = "Pickup request submitted successfully! Request ID: #" + pickupRequest.getPickupId();
            return "redirect:/" + username + "/dashboard?success=" + URLEncoder.encode(successMessage, "UTF-8");

        } catch (Exception e) {
            try {
                String errorMessage = "Error creating pickup request: " + e.getMessage();
                return "redirect:/" + username + "/dashboard?error=" + URLEncoder.encode(errorMessage, "UTF-8");
            } catch (Exception ex) {
                return "redirect:/" + username + "/dashboard";
            }
        }
    }

    /**
     * View all pickup requests for the user
     */
    @GetMapping("/history")
    public String viewPickupHistory(@PathVariable String username,
                                    Authentication auth,
                                    Model model) {
        // Security check
        if (auth == null || !auth.getName().equals(username)) {
            return "redirect:/login";
        }

        try {
            List<PickupRequest> pickupRequests = pickupService.getUserPickupRequests(username);
            model.addAttribute("pickupRequests", pickupRequests);
            model.addAttribute("username", username);
            return "user/pickup/history";
        } catch (Exception e) {
            try {
                return "redirect:/" + username + "/dashboard?error=" + URLEncoder.encode("Error loading pickup history", "UTF-8");
            } catch (UnsupportedEncodingException ex) {
                return "redirect:/" + username + "/dashboard";
            }
        }
    }

    /**
     * View specific pickup request details
     */
    @GetMapping("/details/{pickupId}")
    public String viewPickupDetails(@PathVariable String username,
                                    @PathVariable Long pickupId,
                                    Authentication auth,
                                    Model model) {
        // Security check
        if (auth == null || !auth.getName().equals(username)) {
            return "redirect:/login";
        }

        try {
            PickupRequest pickupRequest = pickupService.getPickupRequestById(pickupId);

            // Check if this pickup belongs to the user
            if (!pickupRequest.getUser().getUsername().equals(username)) {
                try {
                    return "redirect:/" + username + "/pickup/history?error=" +
                           URLEncoder.encode("Access denied", "UTF-8");
                } catch (UnsupportedEncodingException ex) {
                    return "redirect:/" + username + "/pickup/history";
                }
            }

            model.addAttribute("pickupRequest", pickupRequest);
            model.addAttribute("username", username);
            return "user/pickup/details";
        } catch (Exception e) {
            try {
                return "redirect:/" + username + "/pickup/history?error=" +
                       URLEncoder.encode("Error loading pickup details", "UTF-8");
            } catch (UnsupportedEncodingException ex) {
                return "redirect:/" + username + "/pickup/history";
            }
        }
    }

    /**
     * Cancel a pickup request
     */
    @PostMapping("/cancel/{pickupId}")
    public String cancelPickupRequest(@PathVariable String username,
                                      @PathVariable Long pickupId,
                                      @RequestParam(required = false) String reason,
                                      Authentication auth,
                                      RedirectAttributes redirectAttributes) {
        // Security check
        if (auth == null || !auth.getName().equals(username)) {
            return "redirect:/login";
        }

        try {
            PickupRequest pickupRequest = pickupService.getPickupRequestById(pickupId);

            // Check if this pickup belongs to the user
            if (!pickupRequest.getUser().getUsername().equals(username)) {
                try {
                    return "redirect:/" + username + "/pickup/history?error=" +
                           URLEncoder.encode("Access denied", "UTF-8");
                } catch (UnsupportedEncodingException ex) {
                    return "redirect:/" + username + "/pickup/history";
                }
            }

            // Check if pickup can be cancelled
            if (pickupRequest.getStatus() == PickupRequest.PickupStatus.COMPLETED ||
                pickupRequest.getStatus() == PickupRequest.PickupStatus.CANCELLED) {
                try {
                    return "redirect:/" + username + "/pickup/history?error=" +
                           URLEncoder.encode("Cannot cancel this pickup request", "UTF-8");
                } catch (UnsupportedEncodingException ex) {
                    return "redirect:/" + username + "/pickup/history";
                }
            }

            pickupService.cancelPickupRequest(pickupId, reason);

            try {
                return "redirect:/" + username + "/pickup/history?success=" +
                       URLEncoder.encode("Pickup request cancelled successfully", "UTF-8");
            } catch (UnsupportedEncodingException ex) {
                return "redirect:/" + username + "/pickup/history";
            }

        } catch (Exception e) {
            try {
                return "redirect:/" + username + "/pickup/history?error=" +
                       URLEncoder.encode("Error cancelling pickup request", "UTF-8");
            } catch (UnsupportedEncodingException ex) {
                return "redirect:/" + username + "/pickup/history";
            }
        }
    }
}

