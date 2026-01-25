package com.example.ecorecycle.controller;

import com.example.ecorecycle.dto.PickupRequestDto;
import com.example.ecorecycle.entity.BaseUser;
import com.example.ecorecycle.entity.PickupRequest;
import com.example.ecorecycle.entity.Role;
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
     * Edit PENDING pickup request - GET (show form)
     */
    @GetMapping("/edit/{pickupId}")
    public String editPickupForm(@PathVariable String username,
                                 @PathVariable Long pickupId,
                                 Authentication auth,
                                 Model model) {
        if (auth == null || !auth.getName().equals(username)) {
            return "redirect:/login";
        }

        try {
            PickupRequest pickupRequest = pickupService.getPickupRequestById(pickupId);

            // Verify order belongs to this user and is PENDING
            if (!pickupRequest.getUser().getUsername().equals(username)) {
                return "redirect:/" + username + "/pickup/my-orders?error=" + URLEncoder.encode("Unauthorized access", "UTF-8");
            }

            if (pickupRequest.getStatus() != PickupRequest.PickupStatus.PENDING) {
                return "redirect:/" + username + "/pickup/my-orders?error=" + URLEncoder.encode("Can only edit pending orders", "UTF-8");
            }

            model.addAttribute("order", pickupRequest);
            model.addAttribute("username", username);
            model.addAttribute("plasticTypesArray", pickupRequest.getPlasticTypes().split(","));
            return "user/pickup/edit-order";
        } catch (Exception e) {
            try {
                return "redirect:/" + username + "/pickup/my-orders?error=" + URLEncoder.encode("Error loading edit form: " + e.getMessage(), "UTF-8");
            } catch (UnsupportedEncodingException ex) {
                return "redirect:/" + username + "/pickup/my-orders";
            }
        }
    }

    /**
     * Edit PENDING pickup request - POST (save changes)
     */
    @PostMapping("/edit/{pickupId}")
    public String updatePickupRequest(@PathVariable String username,
                                      @PathVariable Long pickupId,
                                      Authentication auth,
                                      @RequestParam List<String> plasticTypes,
                                      @RequestParam Double approxWeight,
                                      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate preferredDate,
                                      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime preferredTime,
                                      @RequestParam(required = false) String additionalNotes) {
        if (auth == null || !auth.getName().equals(username)) {
            return "redirect:/login";
        }

        try {
            PickupRequest pickupRequest = pickupService.getPickupRequestById(pickupId);

            // Verify order belongs to this user and is PENDING
            if (!pickupRequest.getUser().getUsername().equals(username)) {
                return "redirect:/" + username + "/pickup/my-orders?error=" + URLEncoder.encode("Unauthorized access", "UTF-8");
            }

            if (pickupRequest.getStatus() != PickupRequest.PickupStatus.PENDING) {
                return "redirect:/" + username + "/pickup/my-orders?error=" + URLEncoder.encode("Can only edit pending orders", "UTF-8");
            }

            // Update the pickup request
            pickupRequest.setPlasticTypes(String.join(",", plasticTypes));
            pickupRequest.setApproxWeight(approxWeight);
            pickupRequest.setPreferredDate(preferredDate);
            pickupRequest.setPreferredTime(preferredTime);
            pickupRequest.setAdditionalNotes(additionalNotes);
            pickupService.savePickupRequest(pickupRequest);

            return "redirect:/" + username + "/pickup/my-orders?success=" + URLEncoder.encode("Order updated successfully", "UTF-8");
        } catch (Exception e) {
            try {
                return "redirect:/" + username + "/pickup/my-orders?error=" + URLEncoder.encode("Error updating order: " + e.getMessage(), "UTF-8");
            } catch (UnsupportedEncodingException ex) {
                return "redirect:/" + username + "/pickup/my-orders";
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
     * Recycler - View individual order details from history
     */
    @GetMapping("/order-history/{pickupId}")
    public String viewRecyclerOrderHistoryDetail(@PathVariable String username,
                                                 @PathVariable Long pickupId,
                                                 Authentication auth,
                                                 Model model) {
        if (auth == null || !auth.getName().equals(username)) {
            return "redirect:/login";
        }
        try {
            PickupRequest order = pickupService.getPickupRequestById(pickupId);

            // Verify order belongs to this recycler
            if (order.getRecycler() == null || !order.getRecycler().getUsername().equals(username)) {
                return "redirect:/" + username + "/pickup/order-history?error=" + URLEncoder.encode("Unauthorized access", "UTF-8");
            }

            model.addAttribute("order", order);
            model.addAttribute("username", username);
            return "user/pickup/order-details";
        } catch (Exception e) {
            try {
                return "redirect:/" + username + "/pickup/order-history?error=" + URLEncoder.encode("Error loading order details", "UTF-8");
            } catch (UnsupportedEncodingException ex) {
                return "redirect:/" + username + "/pickup/order-history";
            }
        }
    }

    /**
     * View active/pending orders for user (household/business)
     * Shows:
     * - PENDING, ACCEPTED, SCHEDULED, IN_PROGRESS, DELAYED (with cancel option)
     * - COMPLETED (unacknowledged) - with rate/ok options
     * - CANCELLED by RECYCLER only (unacknowledged) - with ok option to clear
     * - Does NOT show orders cancelled by USER
     */
    @GetMapping("/my-orders")
    public String viewMyOrders(@PathVariable String username,
                               Authentication auth,
                               Model model) {
        if (auth == null || !auth.getName().equals(username)) {
            return "redirect:/login";
        }

        try {
            List<PickupRequest> pickupRequests = pickupService.getUserPickupRequests(username);

            // Filter for:
            // 1. Active running orders (PENDING, ACCEPTED, SCHEDULED, IN_PROGRESS, DELAYED)
            // 2. COMPLETED orders that haven't been acknowledged
            // 3. CANCELLED orders by RECYCLER that haven't been acknowledged
            // 4. EXCLUDE orders cancelled by USER
            List<PickupRequest> activeOrders = pickupRequests.stream()
                    .filter(pr -> {
                        // Show if not acknowledged (null or false)
                        boolean notAcknowledged = pr.getAcknowledged() == null || !pr.getAcknowledged();

                        // Show running orders
                        if (pr.getStatus() == PickupRequest.PickupStatus.PENDING ||
                            pr.getStatus() == PickupRequest.PickupStatus.ACCEPTED ||
                            pr.getStatus() == PickupRequest.PickupStatus.SCHEDULED ||
                            pr.getStatus() == PickupRequest.PickupStatus.IN_PROGRESS ||
                            pr.getStatus() == PickupRequest.PickupStatus.DELAYED) {
                            return true;
                        }

                        // Show completed if not acknowledged
                        if (pr.getStatus() == PickupRequest.PickupStatus.COMPLETED && notAcknowledged) {
                            return true;
                        }

                        // Show cancelled ONLY if cancelled by RECYCLER and not acknowledged
                        if (pr.getStatus() == PickupRequest.PickupStatus.CANCELLED &&
                            "RECYCLER".equals(pr.getCancelledBy()) && notAcknowledged) {
                            return true;
                        }

                        return false;
                    })
                    .toList();

            model.addAttribute("orders", activeOrders);
            model.addAttribute("username", username);
            return "user/pickup/my-orders";
        } catch (Exception e) {
            e.printStackTrace(); // Log the error
            try {
                return "redirect:/" + username + "/dashboard?error=" + URLEncoder.encode("Error loading orders: " + e.getMessage(), "UTF-8");
            } catch (UnsupportedEncodingException ex) {
                return "redirect:/" + username + "/dashboard";
            }
        }
    }

    /**
     * View order history for user/recycler (completed/cancelled orders)
     * Universal endpoint that handles both users and recyclers
     */
    @GetMapping("/order-history")
    public String viewOrderHistory(@PathVariable String username,
                                   Authentication auth,
                                   Model model) {
        if (auth == null || !auth.getName().equals(username)) {
            return "redirect:/login";
        }

        try {
            // Get the user to check their role
            BaseUser user = pickupService.getUserByUsername(username);
            List<PickupRequest> orders;
            String templatePath;

            if (user.getRole() == Role.ROLE_RECYCLER) {
                // Recycler: get their completed and cancelled pickups
                orders = pickupService.getCompletedAndCancelledPickupsForRecycler(username);
                // Ensure eco points are present for each completed order
                orders = orders.stream()
                        .map(pickupService::ensureEcoPointsAwarded)
                        .toList();
                templatePath = "user/recycler/order-history";
            } else {
                // User (Household/Business): get their completed and cancelled requests
                List<PickupRequest> pickupRequests = pickupService.getUserPickupRequests(username);
                orders = pickupRequests.stream()
                        .filter(pr -> pr.getStatus() == PickupRequest.PickupStatus.COMPLETED
                                   || pr.getStatus() == PickupRequest.PickupStatus.CANCELLED)
                        .map(pickupService::ensureEcoPointsAwarded)
                        .toList();
                templatePath = "user/pickup/order-history";
            }

            model.addAttribute("orders", orders);
            model.addAttribute("username", username);
            return templatePath;
        } catch (Exception e) {
            try {
                return "redirect:/" + username + "/dashboard?error=" + URLEncoder.encode("Error loading order history", "UTF-8");
            } catch (UnsupportedEncodingException ex) {
                return "redirect:/" + username + "/dashboard";
            }
        }
    }

    /**
     * View single order details
     */
    @GetMapping("/order/{orderId}")
    public String viewOrderDetails(@PathVariable String username,
                                   @PathVariable Long orderId,
                                   Authentication auth,
                                   Model model) {
        if (auth == null || !auth.getName().equals(username)) {
            return "redirect:/login";
        }

        try {
            PickupRequest order = pickupService.getPickupRequestById(orderId);

            // Verify order belongs to this user
            if (!order.getUser().getUsername().equals(username)) {
                return "redirect:/" + username + "/pickup/my-orders?error=" + URLEncoder.encode("Unauthorized access", "UTF-8");
            }

            // Ensure eco points are backfilled for display
            order = pickupService.ensureEcoPointsAwarded(order);

            model.addAttribute("order", order);
            model.addAttribute("username", username);
            return "user/pickup/order-details";
        } catch (Exception e) {
            try {
                return "redirect:/" + username + "/pickup/history?error=" + URLEncoder.encode("Error loading pickup details", "UTF-8");
            } catch (UnsupportedEncodingException ex) {
                return "redirect:/" + username + "/pickup/history";
            }
        }
    }

    /**
     * Cancel an order (user can cancel if not completed)
     */
    @PostMapping("/order/{orderId}/cancel")
    public String cancelOrder(@PathVariable String username,
                             @PathVariable Long orderId,
                             @RequestParam(required = false) String reason,
                             Authentication auth,
                             RedirectAttributes redirectAttributes) {
        if (auth == null || !auth.getName().equals(username)) {
            return "redirect:/login";
        }

        try {
            PickupRequest order = pickupService.getPickupRequestById(orderId);

            // Verify order belongs to this user
            if (!order.getUser().getUsername().equals(username)) {
                return "redirect:/" + username + "/pickup/my-orders?error=" + URLEncoder.encode("Unauthorized access", "UTF-8");
            }

            // Check if order can be cancelled
            if (order.getStatus() == PickupRequest.PickupStatus.COMPLETED) {
                return "redirect:/" + username + "/pickup/my-orders?error=" + URLEncoder.encode("Cannot cancel completed order", "UTF-8");
            }

            if (order.getStatus() == PickupRequest.PickupStatus.CANCELLED) {
                return "redirect:/" + username + "/pickup/my-orders?error=" + URLEncoder.encode("Order already cancelled", "UTF-8");
            }

            // Cancel the order
            pickupService.cancelPickupRequest(orderId, reason != null ? reason : "Cancelled by user");

            return "redirect:/" + username + "/pickup/my-orders?success=" + URLEncoder.encode("Order cancelled successfully", "UTF-8");
        } catch (Exception e) {
            try {
                return "redirect:/" + username + "/pickup/my-orders?error=" + URLEncoder.encode("Error cancelling order: " + e.getMessage(), "UTF-8");
            } catch (UnsupportedEncodingException ex) {
                return "redirect:/" + username + "/pickup/my-orders";
            }
        }
    }

    /**
     * Submit review for completed order
     */
    @PostMapping("/order/{orderId}/review")
    public String submitReview(@PathVariable String username,
                              @PathVariable Long orderId,
                              @RequestParam Integer rating,
                              @RequestParam(required = false) String comment,
                              Authentication auth) {
        if (auth == null || !auth.getName().equals(username)) {
            return "redirect:/login";
        }

        try {
            PickupRequest order = pickupService.getPickupRequestById(orderId);

            // Verify order belongs to this user
            if (!order.getUser().getUsername().equals(username)) {
                return "redirect:/" + username + "/pickup/my-orders?error=" + URLEncoder.encode("Unauthorized access", "UTF-8");
            }

            // Check if order is completed
            if (order.getStatus() != PickupRequest.PickupStatus.COMPLETED) {
                return "redirect:/" + username + "/pickup/my-orders?error=" + URLEncoder.encode("Can only review completed orders", "UTF-8");
            }

            // Check if already reviewed
            if (order.getRating() != null) {
                return "redirect:/" + username + "/pickup/my-orders?error=" + URLEncoder.encode("Order already reviewed", "UTF-8");
            }

            // Submit review
            pickupService.submitReview(orderId, rating, comment);

            return "redirect:/" + username + "/pickup/my-orders?success=" + URLEncoder.encode("Review submitted successfully! Thank you for your feedback.", "UTF-8");
        } catch (Exception e) {
            try {
                return "redirect:/" + username + "/pickup/my-orders?error=" + URLEncoder.encode("Error submitting review: " + e.getMessage(), "UTF-8");
            } catch (UnsupportedEncodingException ex) {
                return "redirect:/" + username + "/pickup/my-orders";
            }
        }
    }

    /**
     * Acknowledge completed order (mark as seen and move to history)
     */
    @PostMapping("/order/{orderId}/acknowledge")
    public String acknowledgeOrder(@PathVariable String username,
                                   @PathVariable Long orderId,
                                   Authentication auth) {
        if (auth == null || !auth.getName().equals(username)) {
            return "redirect:/login";
        }

        try {
            PickupRequest order = pickupService.getPickupRequestById(orderId);

            // Verify order belongs to this user
            if (!order.getUser().getUsername().equals(username)) {
                return "redirect:/" + username + "/pickup/my-orders?error=" + URLEncoder.encode("Unauthorized access", "UTF-8");
            }

            // Check if order is completed
            if (order.getStatus() != PickupRequest.PickupStatus.COMPLETED) {
                return "redirect:/" + username + "/pickup/my-orders?error=" + URLEncoder.encode("Can only acknowledge completed orders", "UTF-8");
            }

            // Acknowledge order
            pickupService.acknowledgeOrder(orderId);

            return "redirect:/" + username + "/pickup/my-orders?success=" + URLEncoder.encode("Order acknowledged successfully", "UTF-8");
        } catch (Exception e) {
            try {
                return "redirect:/" + username + "/pickup/my-orders?error=" + URLEncoder.encode("Error acknowledging order: " + e.getMessage(), "UTF-8");
            } catch (UnsupportedEncodingException ex) {
                return "redirect:/" + username + "/pickup/my-orders";
            }
        }
    }
}
