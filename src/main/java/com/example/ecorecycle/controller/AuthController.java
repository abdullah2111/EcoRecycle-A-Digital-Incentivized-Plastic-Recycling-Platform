package com.example.ecorecycle.controller;

import com.example.ecorecycle.dto.RecyclerRegistrationDto;
import com.example.ecorecycle.dto.UserRegistrationDto;
import com.example.ecorecycle.entity.Role;
import com.example.ecorecycle.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @GetMapping("/login")
    public String loginPage() {
        return "auth/login";
    }

    @GetMapping("/register")
    public String userRegisterPage(Model model) {
        model.addAttribute("user", new UserRegistrationDto());
        model.addAttribute("userRoles", new Role[]{Role.ROLE_HOUSEHOLD, Role.ROLE_BUSINESS});
        return "auth/register-user";
    }

    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("user") UserRegistrationDto dto,
                               BindingResult bindingResult,
                               RedirectAttributes redirectAttributes,
                               Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("userRoles", new Role[]{Role.ROLE_HOUSEHOLD, Role.ROLE_BUSINESS});
            return "auth/register-user";
        }

        try {
            authService.registerUser(dto);
            redirectAttributes.addFlashAttribute("success", "Registration successful! Please login.");
            return "redirect:/login";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            model.addAttribute("userRoles", new Role[]{Role.ROLE_HOUSEHOLD, Role.ROLE_BUSINESS});
            return "auth/register-user";
        }
    }

    @GetMapping("/register/recycler")
    public String recyclerRegisterPage(Model model) {
        model.addAttribute("recycler", new RecyclerRegistrationDto());
        return "auth/register-recycler";
    }

    @PostMapping("/register/recycler")
    public String registerRecycler(@Valid @ModelAttribute("recycler") RecyclerRegistrationDto dto,
                                   BindingResult bindingResult,
                                   RedirectAttributes redirectAttributes,
                                   Model model) {
        if (bindingResult.hasErrors()) {
            return "auth/register-recycler";
        }

        try {
            authService.registerRecycler(dto);
            redirectAttributes.addFlashAttribute("success", "Recycler registered successfully! Please login.");
            return "redirect:/login";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "auth/register-recycler";
        }
    }
}
