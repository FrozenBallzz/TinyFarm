package com.tinyfarm.controller;

import com.tinyfarm.dto.DashboardResponse;
import com.tinyfarm.service.FarmKernelService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    private final FarmKernelService farmKernelService;

    public DashboardController(FarmKernelService farmKernelService) {
        this.farmKernelService = farmKernelService;
    }

    @GetMapping("/")
    public String home(Model model, @AuthenticationPrincipal OAuth2User user) {
        model.addAttribute("isAuthenticated", user != null);
        if (user != null) {
            DashboardResponse dashboard = farmKernelService.getDashboard(user.<String>getAttribute("login"));
            model.addAttribute("dashboard", dashboard);
        }
        return "index";
    }
}
