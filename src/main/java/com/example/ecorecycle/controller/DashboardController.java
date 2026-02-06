package com.example.ecorecycle.controller;

import com.example.ecorecycle.entity.BaseUser;
import com.example.ecorecycle.entity.PickupRequest;
import com.example.ecorecycle.entity.RecyclerProfile;
import com.example.ecorecycle.entity.Role;
import com.example.ecorecycle.repository.BaseUserRepository;
import com.example.ecorecycle.repository.HouseholdProfileRepository;
import com.example.ecorecycle.repository.BusinessProfileRepository;
import com.example.ecorecycle.repository.RecyclerProfileRepository;
import com.example.ecorecycle.service.PickupService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class DashboardController {

    private final BaseUserRepository baseUserRepository;
    private final HouseholdProfileRepository householdProfileRepository;
    private final BusinessProfileRepository businessProfileRepository;
    private final RecyclerProfileRepository recyclerProfileRepository;
    private final PickupService pickupService;

    @GetMapping("/{username}/dashboard")
    public String dashboard(@PathVariable String username, Authentication auth, Model model) {
        if (auth == null) {
            return "redirect:/login";
        }
        // Prevent accessing other users' dashboards
        if (!auth.getName().equals(username)) {
            return "redirect:/" + auth.getName() + "/dashboard";
        }

        BaseUser user = baseUserRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        model.addAttribute("user", user);
        model.addAttribute("username", username);
        model.addAttribute("userId", user.getUserId());

        // Route to appropriate dashboard based on role
        if (user.getRole() == Role.ROLE_HOUSEHOLD) {
            model.addAttribute("profile", user.getHouseholdProfile());
            return "user/household/dashboard";
        } else if (user.getRole() == Role.ROLE_BUSINESS) {
            model.addAttribute("profile", user.getBusinessProfile());

            // Calculate business statistics
            List<PickupRequest> allPickups = pickupService.getUserPickupRequests(username);

            // Total completed pickups
            long totalPickups = allPickups.stream()
                    .filter(p -> p.getStatus() == PickupRequest.PickupStatus.COMPLETED)
                    .count();

            // Total pending/active requests
            long pendingPickups = allPickups.stream()
                    .filter(p -> p.getStatus() != PickupRequest.PickupStatus.COMPLETED
                            && p.getStatus() != PickupRequest.PickupStatus.CANCELLED)
                    .count();

            // Total waste recycled (kg)
            double totalWaste = allPickups.stream()
                    .filter(p -> p.getStatus() == PickupRequest.PickupStatus.COMPLETED)
                    .mapToDouble(p -> p.getApproxWeight() != null ? p.getApproxWeight() : 0)
                    .sum();

            // This month stats
            LocalDateTime monthStart = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
            long thisMonthPickups = allPickups.stream()
                    .filter(p -> p.getStatus() == PickupRequest.PickupStatus.COMPLETED
                            && p.getCompletedAt() != null
                            && p.getCompletedAt().isAfter(monthStart))
                    .count();

            double thisMonthWaste = allPickups.stream()
                    .filter(p -> p.getStatus() == PickupRequest.PickupStatus.COMPLETED
                            && p.getCompletedAt() != null
                            && p.getCompletedAt().isAfter(monthStart))
                    .mapToDouble(p -> p.getApproxWeight() != null ? p.getApproxWeight() : 0)
                    .sum();

            // Average weight per pickup
            double avgWeight = totalPickups > 0 ? totalWaste / totalPickups : 0;

            // Pickup summary
            long cancelledPickups = allPickups.stream()
                    .filter(p -> p.getStatus() == PickupRequest.PickupStatus.CANCELLED)
                    .count();
            LocalDateTime lastPickupDate = allPickups.stream()
                    .filter(p -> p.getStatus() == PickupRequest.PickupStatus.COMPLETED)
                    .map(PickupRequest::getCompletedAt)
                    .filter(java.util.Objects::nonNull)
                    .max(LocalDateTime::compareTo)
                    .orElse(null);

            // Eco points summary
            long pointsThisMonth = allPickups.stream()
                    .filter(p -> p.getStatus() == PickupRequest.PickupStatus.COMPLETED
                            && p.getCompletedAt() != null
                            && p.getCompletedAt().isAfter(monthStart))
                    .mapToLong(p -> p.getEcoPointsAwarded() != null ? p.getEcoPointsAwarded() : 0L)
                    .sum();
            long totalPoints = allPickups.stream()
                    .filter(p -> p.getStatus() == PickupRequest.PickupStatus.COMPLETED)
                    .mapToLong(p -> p.getEcoPointsAwarded() != null ? p.getEcoPointsAwarded() : 0L)
                    .sum();
            double avgPointsPerPickup = totalPickups > 0 ? (double) totalPoints / totalPickups : 0;
            long pointsRedeemed = 0L;

            // Plastic type totals (kg) for completed pickups
            java.util.Map<String, Double> plasticTypeKg = new java.util.LinkedHashMap<>();
            for (String type : new String[] {"PET", "HDPE", "PVC", "LDPE", "PP", "PS", "OTHER"}) {
                plasticTypeKg.put(type, 0.0);
            }
            for (PickupRequest pickup : allPickups) {
                if (pickup.getStatus() != PickupRequest.PickupStatus.COMPLETED) {
                    continue;
                }
                if (pickup.getPlasticTypes() == null || pickup.getApproxWeight() == null) {
                    continue;
                }
                String[] types = pickup.getPlasticTypes().split(",");
                int typeCount = 0;
                for (String t : types) {
                    if (t != null && !t.trim().isEmpty()) {
                        typeCount++;
                    }
                }
                if (typeCount == 0) {
                    continue;
                }
                double perTypeWeight = pickup.getApproxWeight() / typeCount;
                for (String t : types) {
                    String key = t != null ? t.trim() : "";
                    if (plasticTypeKg.containsKey(key)) {
                        plasticTypeKg.put(key, plasticTypeKg.get(key) + perTypeWeight);
                    }
                }
            }

            model.addAttribute("totalPickups", totalPickups);
            model.addAttribute("pendingPickups", pendingPickups);
            model.addAttribute("totalWasteRecycled", totalWaste);
            model.addAttribute("thisMonthPickups", thisMonthPickups);
            model.addAttribute("thisMonthWaste", thisMonthWaste);
            model.addAttribute("avgWeightPerPickup", avgWeight);
            model.addAttribute("lastPickupDate", lastPickupDate);
            model.addAttribute("completedPickups", totalPickups);
            model.addAttribute("cancelledPickups", cancelledPickups);
            model.addAttribute("pointsThisMonth", pointsThisMonth);
            model.addAttribute("pointsRedeemed", pointsRedeemed);
            model.addAttribute("avgPointsPerPickup", avgPointsPerPickup);
            model.addAttribute("plasticTypeKg", plasticTypeKg);

            return "user/business/dashboard";
        } else if (user.getRole() == Role.ROLE_RECYCLER) {
            RecyclerProfile profile = user.getRecyclerProfile();
            model.addAttribute("profile", profile);

            // Calculate recycler statistics
            List<PickupRequest> allPickups = pickupService.getCompletedAndCancelledPickupsForRecycler(username);
            List<PickupRequest> assignedPickups = pickupService.getAssignedPickupsForRecycler(username);

            // Total completed pickups
            long completedPickups = allPickups.stream()
                    .filter(p -> p.getStatus() == PickupRequest.PickupStatus.COMPLETED)
                    .count();

            // Total pending/active requests
            long pendingPickups = assignedPickups.size();

            // Total waste recycled (kg)
            double totalWaste = allPickups.stream()
                    .filter(p -> p.getStatus() == PickupRequest.PickupStatus.COMPLETED)
                    .mapToDouble(p -> p.getApproxWeight() != null ? p.getApproxWeight() : 0)
                    .sum();

            // This month stats
            LocalDateTime monthStart = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
            long thisMonthPickups = allPickups.stream()
                    .filter(p -> p.getStatus() == PickupRequest.PickupStatus.COMPLETED
                            && p.getCompletedAt() != null
                            && p.getCompletedAt().isAfter(monthStart))
                    .count();

            double thisMonthWaste = allPickups.stream()
                    .filter(p -> p.getStatus() == PickupRequest.PickupStatus.COMPLETED
                            && p.getCompletedAt() != null
                            && p.getCompletedAt().isAfter(monthStart))
                    .mapToDouble(p -> p.getApproxWeight() != null ? p.getApproxWeight() : 0)
                    .sum();

            // Average weight per pickup
            double avgWeight = completedPickups > 0 ? totalWaste / completedPickups : 0;

            model.addAttribute("completedPickups", completedPickups);
            model.addAttribute("pendingPickups", pendingPickups);
            model.addAttribute("totalWasteRecycled", totalWaste);
            model.addAttribute("thisMonthPickups", thisMonthPickups);
            model.addAttribute("thisMonthWaste", thisMonthWaste);
            model.addAttribute("avgWeightPerPickup", avgWeight);

            return "user/recycler/dashboard";
        } else if (user.getRole() == Role.ROLE_ADMIN) {
            return "user/admin/dashboard";
        }

        return "redirect:/login";
    }

    @GetMapping("/{username}/profile")
    public String profile(@PathVariable String username, Authentication auth, Model model) {
        if (auth == null) {
            return "redirect:/login";
        }
        // Prevent accessing other users' profiles
        if (!auth.getName().equals(username)) {
            return "redirect:/" + auth.getName() + "/profile";
        }

        BaseUser user = baseUserRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        model.addAttribute("user", user);
        model.addAttribute("username", username);
        model.addAttribute("userId", user.getUserId());

        // Route to appropriate profile based on role
        if (user.getRole() == Role.ROLE_HOUSEHOLD) {
            model.addAttribute("profile", user.getHouseholdProfile());
            return "user/household/profile";
        } else if (user.getRole() == Role.ROLE_BUSINESS) {
            model.addAttribute("profile", user.getBusinessProfile());
            return "user/business/profile";
        } else if (user.getRole() == Role.ROLE_RECYCLER) {
            model.addAttribute("profile", user.getRecyclerProfile());
            return "user/recycler/profile";
        }

        return "redirect:/login";
    }

    // Profile Update Endpoints for Household
    @PostMapping("/{username}/profile/update/personal")
    public String updateHouseholdPersonal(@PathVariable String username, Authentication auth,
                                          @RequestParam String name,
                                          @RequestParam String phone,
                                          @RequestParam(required = false) String gender,
                                          RedirectAttributes redirectAttributes) {
        if (auth == null || !auth.getName().equals(username)) {
            return "redirect:/login";
        }

        try {
            BaseUser user = baseUserRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            if (user.getHouseholdProfile() != null) {
                var profile = user.getHouseholdProfile();
                profile.setName(name);
                profile.setPhone(phone);
                if (gender != null && !gender.isEmpty()) {
                    profile.setGender(gender);
                }
                householdProfileRepository.save(profile);
                return "redirect:/" + username + "/profile?success=" + java.net.URLEncoder.encode("Personal information updated successfully!", "UTF-8");
            }
        } catch (Exception e) {
            try {
                return "redirect:/" + username + "/profile?error=" + java.net.URLEncoder.encode("Error updating profile: " + e.getMessage(), "UTF-8");
            } catch (Exception ex) {
                return "redirect:/" + username + "/profile";
            }
        }
        return "redirect:/" + username + "/profile";
    }

    @PostMapping("/{username}/profile/update/address")
    public String updateAddress(@PathVariable String username, Authentication auth,
                               @RequestParam String district,
                               @RequestParam String thana,
                               @RequestParam String area,
                               @RequestParam String addressLine,
                               RedirectAttributes redirectAttributes) {
        if (auth == null || !auth.getName().equals(username)) {
            return "redirect:/login";
        }

        try {
            BaseUser user = baseUserRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            if (user.getHouseholdProfile() != null) {
                var profile = user.getHouseholdProfile();
                profile.setDistrict(district);
                profile.setThana(thana);
                profile.setArea(area);
                profile.setAddressLine(addressLine);
                householdProfileRepository.save(profile);
            } else if (user.getBusinessProfile() != null) {
                var profile = user.getBusinessProfile();
                profile.setDistrict(district);
                profile.setThana(thana);
                profile.setArea(area);
                profile.setAddressLine(addressLine);
                businessProfileRepository.save(profile);
            } else if (user.getRecyclerProfile() != null) {
                var profile = user.getRecyclerProfile();
                profile.setDistrict(district);
                profile.setThana(thana);
                profile.setArea(area);
                profile.setAddressLine(addressLine);
                recyclerProfileRepository.save(profile);
            }

            return "redirect:/" + username + "/profile?success=" + java.net.URLEncoder.encode("Address updated successfully!", "UTF-8");
        } catch (Exception e) {
            try {
                return "redirect:/" + username + "/profile?error=" + java.net.URLEncoder.encode("Error updating address: " + e.getMessage(), "UTF-8");
            } catch (Exception ex) {
                return "redirect:/" + username + "/profile";
            }
        }
    }

    // Profile Update Endpoints for Business
    @PostMapping("/{username}/profile/update/business")
    public String updateBusinessInfo(@PathVariable String username, Authentication auth,
                                     @RequestParam String businessName,
                                     @RequestParam String businessType,
                                     @RequestParam String phone,
                                     RedirectAttributes redirectAttributes) {
        if (auth == null || !auth.getName().equals(username)) {
            return "redirect:/login";
        }

        try {
            BaseUser user = baseUserRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            if (user.getBusinessProfile() != null) {
                var profile = user.getBusinessProfile();
                profile.setBusinessName(businessName);
                profile.setBusinessType(businessType);
                profile.setPhone(phone);
                businessProfileRepository.save(profile);
                return "redirect:/" + username + "/profile?success=" + java.net.URLEncoder.encode("Business information updated successfully!", "UTF-8");
            }
        } catch (Exception e) {
            try {
                return "redirect:/" + username + "/profile?error=" + java.net.URLEncoder.encode("Error updating profile: " + e.getMessage(), "UTF-8");
            } catch (Exception ex) {
                return "redirect:/" + username + "/profile";
            }
        }
        return "redirect:/" + username + "/profile";
    }

    // Profile Update Endpoints for Recycler
    @PostMapping("/{username}/profile/update/operations")
    public String updateRecyclerOperations(@PathVariable String username, Authentication auth,
                                           @RequestParam String operatingHours,
                                           @RequestParam String pickupSchedule,
                                           @RequestParam String acceptedPlasticTypes,
                                           RedirectAttributes redirectAttributes) {
        if (auth == null || !auth.getName().equals(username)) {
            return "redirect:/login";
        }

        try {
            BaseUser user = baseUserRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            if (user.getRecyclerProfile() != null) {
                var profile = user.getRecyclerProfile();
                profile.setOperatingHours(operatingHours);
                profile.setPickupSchedule(pickupSchedule);
                profile.setAcceptedPlasticTypes(acceptedPlasticTypes);
                recyclerProfileRepository.save(profile);
                return "redirect:/" + username + "/profile?success=" + java.net.URLEncoder.encode("Operations information updated successfully!", "UTF-8");
            }
        } catch (Exception e) {
            try {
                return "redirect:/" + username + "/profile?error=" + java.net.URLEncoder.encode("Error updating profile: " + e.getMessage(), "UTF-8");
            } catch (Exception ex) {
                return "redirect:/" + username + "/profile";
            }
        }
        return "redirect:/" + username + "/profile";
    }

    // Profile Update Endpoints for Recycler - Center Information
    @PostMapping("/{username}/profile/update/center")
    public String updateRecyclerCenter(@PathVariable String username, Authentication auth,
                                       @RequestParam String name,
                                       @RequestParam String phone,
                                       @RequestParam Double recyclingCapacity,
                                       RedirectAttributes redirectAttributes) {
        if (auth == null || !auth.getName().equals(username)) {
            return "redirect:/login";
        }

        try {
            BaseUser user = baseUserRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            if (user.getRecyclerProfile() != null) {
                var profile = user.getRecyclerProfile();
                profile.setName(name);
                profile.setPhone(phone);
                profile.setRecyclingCapacity(recyclingCapacity);
                recyclerProfileRepository.save(profile);
                return "redirect:/" + username + "/profile?success=" + java.net.URLEncoder.encode("Center information updated successfully!", "UTF-8");
            }
        } catch (Exception e) {
            try {
                return "redirect:/" + username + "/profile?error=" + java.net.URLEncoder.encode("Error updating profile: " + e.getMessage(), "UTF-8");
            } catch (Exception ex) {
                return "redirect:/" + username + "/profile";
            }
        }
        return "redirect:/" + username + "/profile";
    }
}
