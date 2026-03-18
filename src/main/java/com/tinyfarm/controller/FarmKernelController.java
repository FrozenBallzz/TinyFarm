package com.tinyfarm.controller;

import com.tinyfarm.dto.CowActionResponse;
import com.tinyfarm.dto.DashboardResponse;
import com.tinyfarm.dto.RenameCowRequest;
import com.tinyfarm.service.FarmKernelService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class FarmKernelController {

    private final FarmKernelService farmKernelService;

    public FarmKernelController(FarmKernelService farmKernelService) {
        this.farmKernelService = farmKernelService;
    }

    @GetMapping("/dashboard")
    public DashboardResponse dashboard(@AuthenticationPrincipal OAuth2User user) {
        return farmKernelService.getDashboard(user.<String>getAttribute("login"));
    }

    @PostMapping("/cows/{cowId}/feed")
    public CowActionResponse feedCow(@PathVariable Long cowId, @AuthenticationPrincipal OAuth2User user) {
        return farmKernelService.feedCow(user.<String>getAttribute("login"), cowId);
    }

    @PostMapping("/cows/{cowId}/water")
    public CowActionResponse waterCow(@PathVariable Long cowId, @AuthenticationPrincipal OAuth2User user) {
        return farmKernelService.waterCow(user.<String>getAttribute("login"), cowId);
    }

    @PostMapping("/cows/{cowId}/clean")
    public CowActionResponse cleanCow(@PathVariable Long cowId, @AuthenticationPrincipal OAuth2User user) {
        return farmKernelService.cleanCow(user.<String>getAttribute("login"), cowId);
    }

    @PostMapping("/cows/{cowId}/heal")
    public CowActionResponse healCow(@PathVariable Long cowId, @AuthenticationPrincipal OAuth2User user) {
        return farmKernelService.healCow(user.<String>getAttribute("login"), cowId);
    }

    @PostMapping("/cows/{cowId}/rename")
    public CowActionResponse renameCow(
        @PathVariable Long cowId,
        @AuthenticationPrincipal OAuth2User user,
        @Valid @ModelAttribute RenameCowRequest renameCowRequest
    ) {
        return farmKernelService.renameCow(user.<String>getAttribute("login"), cowId, renameCowRequest);
    }

    @PostMapping("/cows/{cowId}/advance-day")
    public CowActionResponse advanceCowDay(@PathVariable Long cowId, @AuthenticationPrincipal OAuth2User user) {
        return farmKernelService.advanceCowDay(user.<String>getAttribute("login"), cowId);
    }

    @PostMapping("/cows/{cowId}/collect-milk")
    public CowActionResponse collectMilk(@PathVariable Long cowId, @AuthenticationPrincipal OAuth2User user) {
        return farmKernelService.collectMilk(user.<String>getAttribute("login"), cowId);
    }
}
