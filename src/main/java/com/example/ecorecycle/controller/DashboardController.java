package com.example.ecorecycle.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    @GetMapping("/dashboard")
    public String dashboard(Authentication auth) {
        if (auth == null) return "redirect:/login";
        String username = auth.getName();
        if (auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("RECYCLER"))) {
            return "redirect:/" + username + "/dashboard";
        }
        if (auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ADMIN"))) {
            return "redirect:/" + username + "/dashboard";
        }
        return "redirect:/" + username + "/dashboard";
    }

    @GetMapping("/{username}/dashboard")
    public String userDash(Authentication auth, Model model) {
        model.addAttribute("username", auth.getName());
        model.addAttribute("userId", 0); // TODO: fetch real user id if needed

        if (auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("RECYCLER"))) {
            model.addAttribute("recyclerId", 0); // TODO: fetch real recycler id if needed
            return "dashboard/recycler-dashboard";
        }

        if (auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ADMIN"))) {
            return "dashboard/admin-dashboard";
        }

        return "dashboard/user-dashboard";
    }
}

