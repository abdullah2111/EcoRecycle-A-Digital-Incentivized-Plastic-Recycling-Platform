package com.example.ecorecycle.controller;

import com.example.ecorecycle.dto.RegisterRecyclerDto;
import com.example.ecorecycle.dto.RegisterUserDto;
import com.example.ecorecycle.entity.UserType;
import com.example.ecorecycle.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
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

    @GetMapping("/users/register")
    public String userRegisterPage(Model model) {
        model.addAttribute("user", new RegisterUserDto());
        model.addAttribute("userTypes", UserType.values());
        return "auth/register-user";
    }

    @PostMapping("/users/register")
    public String registerUser(@Validated @ModelAttribute("user") RegisterUserDto dto,
                               BindingResult bindingResult,
                               RedirectAttributes redirectAttributes,
                               Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("userTypes", UserType.values());
            return "auth/register-user";
        }
        authService.registerUser(dto.getName(), dto.getEmail(), dto.getUsername(), dto.getPassword(), dto.getUserType());
        redirectAttributes.addFlashAttribute("success", "User registered. Please login.");
        return "redirect:/login";
    }

    @GetMapping("/recyclers/register")
    public String recyclerRegisterPage(Model model) {
        model.addAttribute("recycler", new RegisterRecyclerDto());
        return "auth/register-recycler";
    }

    @PostMapping("/recyclers/register")
    public String registerRecycler(@Validated @ModelAttribute("recycler") RegisterRecyclerDto dto,
                                   BindingResult bindingResult,
                                   RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "auth/register-recycler";
        }
        authService.registerRecycler(dto.getName(), dto.getEmail(), dto.getPassword(), dto.getServiceArea());
        redirectAttributes.addFlashAttribute("success", "Recycler registered. Please login.");
        return "redirect:/login";
    }
}

