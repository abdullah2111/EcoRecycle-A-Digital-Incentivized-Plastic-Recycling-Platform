package com.example.ecorecycle.controller;

import com.example.ecorecycle.entity.BaseUser;
import com.example.ecorecycle.entity.Role;
import com.example.ecorecycle.repository.BaseUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
@RequiredArgsConstructor
public class DashboardController {

    private final BaseUserRepository baseUserRepository;

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
            return "user/business/dashboard";
        } else if (user.getRole() == Role.ROLE_RECYCLER) {
            model.addAttribute("profile", user.getRecyclerProfile());
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
}

