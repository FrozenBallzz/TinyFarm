package com.tinyfarm.controller;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oauth2Login;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import com.tinyfarm.repository.AppUserRepository;
import com.tinyfarm.repository.FarmRepository;
import com.tinyfarm.service.FarmKernelService;
import com.tinyfarm.service.GithubUserProvisioningService;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class DashboardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private GithubUserProvisioningService provisioningService;

    @Autowired
    private FarmKernelService farmKernelService;

    @Autowired
    private AppUserRepository appUserRepository;

    @Autowired
    private FarmRepository farmRepository;

    @BeforeEach
    void setUp() {
        farmRepository.deleteAll();
        appUserRepository.deleteAll();
        provisioningService.provisionUser(buildUser("tinyfarmer", 42L, "Tiny Farmer"));
    }

    @Test
    void rendersWelcomeScreenForAnonymousUser() throws Exception {
        mockMvc.perform(get("/"))
            .andExpect(status().isOk())
            .andExpect(view().name("index"));
    }

    @Test
    void rendersCowsPageForAuthenticatedUser() throws Exception {
        mockMvc.perform(get("/cows").with(oauth2Login().attributes(attributes -> attributes.put("login", "tinyfarmer"))))
            .andExpect(status().isOk())
            .andExpect(view().name("cows"));
    }

    @Test
    void feedCowFromWebFlowRedirectsBackToBarn() throws Exception {
        Long cowId = farmKernelService.getDashboard("tinyfarmer").cows().get(0).id();

        mockMvc.perform(post("/cows/" + cowId + "/feed")
                .with(oauth2Login().attributes(attributes -> attributes.put("login", "tinyfarmer"))))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/cows"))
            .andExpect(flash().attributeExists("flashMessage"));
    }

    private OAuth2User buildUser(String login, Long id, String name) {
        return new DefaultOAuth2User(
            List.of(),
            java.util.Map.of("login", login, "id", id, "name", name),
            "login"
        );
    }
}
