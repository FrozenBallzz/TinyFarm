package com.tinyfarm.controller;

import com.tinyfarm.dto.DashboardResponse;
import com.tinyfarm.dto.RenameCowRequest;
import com.tinyfarm.service.FarmKernelService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
            DashboardResponse dashboard = farmKernelService.getDashboard(githubLogin(user));
            model.addAttribute("dashboard", dashboard);
            model.addAttribute("githubLogin", githubLogin(user));
        }
        return "index";
    }

    @GetMapping("/cows")
    public String cows(Model model, @AuthenticationPrincipal OAuth2User user) {
        DashboardResponse dashboard = farmKernelService.getDashboard(githubLogin(user));
        model.addAttribute("dashboard", dashboard);
        model.addAttribute("githubLogin", githubLogin(user));
        model.addAttribute("renameCowRequest", new RenameCowRequest(""));
        return "cows";
    }

    @PostMapping("/cows/{cowId}/feed")
    public String feedCow(
        @PathVariable Long cowId,
        @AuthenticationPrincipal OAuth2User user,
        RedirectAttributes redirectAttributes
    ) {
        try {
            farmKernelService.feedCow(githubLogin(user), cowId);
            redirectAttributes.addFlashAttribute("flashMessage", "La vache a bien ete nourrie.");
        } catch (IllegalStateException | IllegalArgumentException exception) {
            redirectAttributes.addFlashAttribute("flashError", exception.getMessage());
        }
        return "redirect:/cows";
    }

    @PostMapping("/cows/{cowId}/water")
    public String waterCow(
        @PathVariable Long cowId,
        @AuthenticationPrincipal OAuth2User user,
        RedirectAttributes redirectAttributes
    ) {
        try {
            farmKernelService.waterCow(githubLogin(user), cowId);
            redirectAttributes.addFlashAttribute("flashMessage", "La vache a bien ete abreuvee.");
        } catch (IllegalStateException | IllegalArgumentException exception) {
            redirectAttributes.addFlashAttribute("flashError", exception.getMessage());
        }
        return "redirect:/cows";
    }

    @PostMapping("/cows/{cowId}/clean")
    public String cleanCow(
        @PathVariable Long cowId,
        @AuthenticationPrincipal OAuth2User user,
        RedirectAttributes redirectAttributes
    ) {
        try {
            farmKernelService.cleanCow(githubLogin(user), cowId);
            redirectAttributes.addFlashAttribute("flashMessage", "La vache a ete nettoyee.");
        } catch (IllegalStateException | IllegalArgumentException exception) {
            redirectAttributes.addFlashAttribute("flashError", exception.getMessage());
        }
        return "redirect:/cows";
    }

    @PostMapping("/cows/{cowId}/heal")
    public String healCow(
        @PathVariable Long cowId,
        @AuthenticationPrincipal OAuth2User user,
        RedirectAttributes redirectAttributes
    ) {
        try {
            farmKernelService.healCow(githubLogin(user), cowId);
            redirectAttributes.addFlashAttribute("flashMessage", "La vache a ete soignee.");
        } catch (IllegalStateException | IllegalArgumentException exception) {
            redirectAttributes.addFlashAttribute("flashError", exception.getMessage());
        }
        return "redirect:/cows";
    }

    @PostMapping("/cows/{cowId}/rename")
    public String renameCow(
        @PathVariable Long cowId,
        @AuthenticationPrincipal OAuth2User user,
        @RequestParam String name,
        RedirectAttributes redirectAttributes
    ) {
        try {
            farmKernelService.renameCow(githubLogin(user), cowId, new RenameCowRequest(name));
            redirectAttributes.addFlashAttribute("flashMessage", "La vache a ete renommee.");
        } catch (IllegalStateException | IllegalArgumentException exception) {
            redirectAttributes.addFlashAttribute("flashError", exception.getMessage());
        }
        return "redirect:/cows";
    }

    @PostMapping("/cows/{cowId}/advance-day")
    public String advanceCowDay(
        @PathVariable Long cowId,
        @AuthenticationPrincipal OAuth2User user,
        RedirectAttributes redirectAttributes
    ) {
        try {
            farmKernelService.advanceCowDay(githubLogin(user), cowId);
            redirectAttributes.addFlashAttribute("flashMessage", "Une journee s'est ecoulee pour la vache.");
        } catch (IllegalStateException | IllegalArgumentException exception) {
            redirectAttributes.addFlashAttribute("flashError", exception.getMessage());
        }
        return "redirect:/cows";
    }

    @PostMapping("/cows/{cowId}/collect-milk")
    public String collectMilk(
        @PathVariable Long cowId,
        @AuthenticationPrincipal OAuth2User user,
        RedirectAttributes redirectAttributes
    ) {
        try {
            farmKernelService.collectMilk(githubLogin(user), cowId);
            redirectAttributes.addFlashAttribute("flashMessage", "Le lait a bien ete collecte.");
        } catch (IllegalStateException | IllegalArgumentException exception) {
            redirectAttributes.addFlashAttribute("flashError", exception.getMessage());
        }
        return "redirect:/cows";
    }

    private String githubLogin(OAuth2User user) {
        return user.getAttribute("login");
    }
}
