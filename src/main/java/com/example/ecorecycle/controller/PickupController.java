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

    /**
     * Recycler - View all pending pickup requests in their service area
     */
    @GetMapping("/requests")
    public String viewPickupRequests(@PathVariable String username,
                                     Authentication auth,
                                     Model model) {
        // Security check
        if (auth == null || !auth.getName().equals(username)) {
            return "redirect:/login";
        }

        try {
            List<PickupRequest> pickupRequests = pickupService.getPendingPickupsForRecycler(username);
            model.addAttribute("pickupRequests", pickupRequests);
            model.addAttribute("username", username);
            return "user/recycler/pickup-requests";
        } catch (Exception e) {
            try {
                return "redirect:/" + username + "/dashboard?error=" +
                       URLEncoder.encode("Error loading pickup requests", "UTF-8");
            } catch (UnsupportedEncodingException ex) {
                return "redirect:/" + username + "/dashboard";
            }
        }
    }

    /**
     * Recycler - Accept a pickup request
     */
    @PostMapping("/accept/{pickupId}")
    public String acceptPickupRequest(@PathVariable String username,
                                      @PathVariable Long pickupId,
                                      Authentication auth) {
        // Security check
        if (auth == null || !auth.getName().equals(username)) {
            return "redirect:/login";
        }

        try {
            pickupService.acceptPickupRequest(pickupId, username);
            return "redirect:/" + username + "/pickup/requests?success=" +
                   URLEncoder.encode("Pickup request accepted successfully!", "UTF-8");
        } catch (Exception e) {
            try {
                return "redirect:/" + username + "/pickup/requests?error=" +
                       URLEncoder.encode("Error accepting pickup request", "UTF-8");
            } catch (UnsupportedEncodingException ex) {
                return "redirect:/" + username + "/pickup/requests";
            }
        }
    }

    /**
     * Recycler - Reject a pickup request
     */
    @PostMapping("/reject/{pickupId}")
    public String rejectPickupRequest(@PathVariable String username,
                                      @PathVariable Long pickupId,
                                      Authentication auth) {
        // Security check
        if (auth == null || !auth.getName().equals(username)) {
            return "redirect:/login";
        }

        try {
            pickupService.rejectPickupRequest(pickupId);
            return "redirect:/" + username + "/pickup/requests?success=" +
                   URLEncoder.encode("Pickup request rejected", "UTF-8");
        } catch (Exception e) {
            try {
                return "redirect:/" + username + "/pickup/requests?error=" +
                       URLEncoder.encode("Error rejecting pickup request", "UTF-8");
            } catch (UnsupportedEncodingException ex) {
                return "redirect:/" + username + "/pickup/requests";
            }
        }
    }

    /**
     * Recycler - View assigned pickups (processing orders)
     */
    @GetMapping("/assigned")
    public String viewAssignedPickups(@PathVariable String username,
                                      Authentication auth,
                                      Model model) {
        if (auth == null || !auth.getName().equals(username)) {
            return "redirect:/login";
        }
        try {
            List<PickupRequest> pickupRequests = pickupService.getAssignedPickupsForRecycler(username);
            model.addAttribute("pickupRequests", pickupRequests);
            model.addAttribute("username", username);
            return "user/recycler/pickup-assigned";
        } catch (Exception e) {
            try {
                return "redirect:/" + username + "/dashboard?error=" + URLEncoder.encode("Error loading assigned pickups", "UTF-8");
            } catch (UnsupportedEncodingException ex) {
                return "redirect:/" + username + "/dashboard";
            }
        }
    }

    /**
     * Recycler - Update status for assigned pickup
     */
    @PostMapping("/status/{pickupId}")
    public String updatePickupStatus(@PathVariable String username,
                                     @PathVariable Long pickupId,
                                     @RequestParam PickupRequest.PickupStatus status,
                                     Authentication auth) {
        if (auth == null || !auth.getName().equals(username)) {
            return "redirect:/login";
        }
        try {
            pickupService.updateRecyclerPickupStatus(pickupId, username, status);
            String statusMsg = status == PickupRequest.PickupStatus.COMPLETED ? "Pickup completed" :
                             status == PickupRequest.PickupStatus.CANCELLED ? "Pickup cancelled" : "Pickup updated";
            return "redirect:/" + username + "/pickup/assigned?success=" + URLEncoder.encode(statusMsg, "UTF-8");
        } catch (Exception e) {
            try {
                return "redirect:/" + username + "/pickup/assigned?error=" + URLEncoder.encode(e.getMessage(), "UTF-8");
            } catch (UnsupportedEncodingException ex) {
                return "redirect:/" + username + "/pickup/assigned";
            }
        }
    }

    /**
     * Recycler - View order history (completed and cancelled pickups)
     */
    @GetMapping("/order-history")
    public String viewOrderHistory(@PathVariable String username,
                                   Authentication auth,
                                   Model model) {
        if (auth == null || !auth.getName().equals(username)) {
            return "redirect:/login";
        }
        try {
            List<PickupRequest> orders = pickupService.getCompletedAndCancelledPickupsForRecycler(username);
            model.addAttribute("orders", orders);
            model.addAttribute("username", username);
            return "user/recycler/order-history";
        } catch (Exception e) {
            try {
                return "redirect:/" + username + "/dashboard?error=" + URLEncoder.encode("Error loading order history", "UTF-8");
            } catch (UnsupportedEncodingException ex) {
                return "redirect:/" + username + "/dashboard";
            }
        }
    }

    /**
     * Recycler - View order details
     */
    @GetMapping("/order-history/{pickupId}")
    public String viewOrderDetails(@PathVariable String username,
                                   @PathVariable Long pickupId,
                                   Authentication auth,
                                   Model model) {
        if (auth == null || !auth.getName().equals(username)) {
            return "redirect:/login";
        }
        try {
            PickupRequest order = pickupService.getPickupRequestById(pickupId);

            // Check if this order belongs to the recycler
            if (order.getRecycler() == null || !order.getRecycler().getUsername().equals(username)) {
                try {
                    return "redirect:/" + username + "/pickup/order-history?error=" + URLEncoder.encode("Access denied", "UTF-8");
                } catch (UnsupportedEncodingException ex) {
                    return "redirect:/" + username + "/pickup/order-history";
                }
            }

            model.addAttribute("order", order);
            model.addAttribute("username", username);
            return "user/recycler/order-details";
        } catch (Exception e) {
            try {
                return "redirect:/" + username + "/pickup/order-history?error=" + URLEncoder.encode("Error loading order details", "UTF-8");
            } catch (UnsupportedEncodingException ex) {
                return "redirect:/" + username + "/pickup/order-history";
            }
        }
    }
}
