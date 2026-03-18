package com.tinyfarm.controller;

import com.tinyfarm.config.DevAuthProperties;
import com.tinyfarm.service.GithubUserProvisioningService;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@ConditionalOnProperty(prefix = "app.dev-auth", name = "enabled", havingValue = "true")
public class DevAuthController {

    private final DevAuthProperties devAuthProperties;
    private final GithubUserProvisioningService provisioningService;

    public DevAuthController(DevAuthProperties devAuthProperties, GithubUserProvisioningService provisioningService) {
        this.devAuthProperties = devAuthProperties;
        this.provisioningService = provisioningService;
    }

    @PostMapping("/dev-login")
    public String devLogin(
        @RequestParam String token,
        @RequestParam String login,
        @RequestParam(required = false) String name,
        @RequestParam(required = false) Long id,
        HttpSession session,
        RedirectAttributes redirectAttributes
    ) {
        if (!devAuthProperties.matches(token)) {
            redirectAttributes.addFlashAttribute("authError", "Invalid dev token.");
            return "redirect:/";
        }

        String trimmedLogin = login == null ? "" : login.trim();
        if (trimmedLogin.isBlank()) {
            redirectAttributes.addFlashAttribute("authError", "Login is required.");
            return "redirect:/";
        }

        OAuth2User user = new DefaultOAuth2User(
            List.of(new SimpleGrantedAuthority("ROLE_USER")),
            Map.of(
                "login", trimmedLogin,
                "id", id != null ? id : Math.abs(trimmedLogin.hashCode()),
                "name", name == null || name.isBlank() ? trimmedLogin : name.trim()
            ),
            "login"
        );

        provisioningService.provisionUser(user);

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
            user,
            "N/A",
            user.getAuthorities()
        );
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
        session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, context);
        return "redirect:/";
    }
}
