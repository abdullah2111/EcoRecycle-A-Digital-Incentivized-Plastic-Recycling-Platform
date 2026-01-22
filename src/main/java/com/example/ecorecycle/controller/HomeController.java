package com.example.ecorecycle.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Home controller for the main landing page
 */
@Controller
public class HomeController {

    @GetMapping({"/", "/index", "/home"})
    public String home(Model model) {
        model.addAttribute("appName", "EcoRecycle");
        model.addAttribute("message", "Welcome to EcoRecycle Platform");
        return "index";
    }

    @GetMapping("/about")
    public String about(Model model) {
        model.addAttribute("appName", "EcoRecycle");
        return "about";
    }
}

