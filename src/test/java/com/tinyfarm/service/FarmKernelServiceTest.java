package com.tinyfarm.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.tinyfarm.dto.CowActionResponse;
import com.tinyfarm.dto.DashboardResponse;
import com.tinyfarm.repository.AppUserRepository;
import com.tinyfarm.repository.FarmRepository;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class FarmKernelServiceTest {

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
    void createsDashboardForProvisionedUser() {
        DashboardResponse dashboard = farmKernelService.getDashboard("tinyfarmer");

        assertThat(dashboard.githubLogin()).isEqualTo("tinyfarmer");
        assertThat(dashboard.feedStock()).isEqualTo(6);
        assertThat(dashboard.milkStock()).isZero();
        assertThat(dashboard.cows()).hasSize(1);
        assertThat(dashboard.cows().get(0).readyToMilk()).isTrue();
    }

    @Test
    void feedCowConsumesFeedAndLeavesCowReadyToMilk() {
        Long cowId = farmKernelService.getDashboard("tinyfarmer").cows().get(0).id();

        CowActionResponse response = farmKernelService.feedCow("tinyfarmer", cowId);

        assertThat(response.action()).isEqualTo("feed");
        assertThat(response.feedStock()).isEqualTo(5);
        assertThat(response.energy()).isEqualTo(70);
        assertThat(response.readyToMilk()).isTrue();
    }

    @Test
    void collectMilkAddsMilkToInventory() {
        Long cowId = farmKernelService.getDashboard("tinyfarmer").cows().get(0).id();

        CowActionResponse response = farmKernelService.collectMilk("tinyfarmer", cowId);

        assertThat(response.action()).isEqualTo("collect-milk");
        assertThat(response.milkStock()).isEqualTo(2);
        assertThat(response.readyToMilk()).isFalse();
    }

    @Test
    void collectMilkFailsWhenCowIsNotReady() {
        Long cowId = farmKernelService.getDashboard("tinyfarmer").cows().get(0).id();
        farmKernelService.collectMilk("tinyfarmer", cowId);

        assertThatThrownBy(() -> farmKernelService.collectMilk("tinyfarmer", cowId))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("not ready");
    }

    @Test
    void provisioningExistingUserDoesNotCreateDuplicateFarm() {
        provisioningService.provisionUser(buildUser("tinyfarmer", 42L, "Tiny Farmer Updated"));

        DashboardResponse dashboard = farmKernelService.getDashboard("tinyfarmer");

        assertThat(appUserRepository.count()).isEqualTo(1);
        assertThat(farmRepository.count()).isEqualTo(1);
        assertThat(dashboard.cows()).hasSize(1);
    }

    private OAuth2User buildUser(String login, Long id, String name) {
        return new DefaultOAuth2User(
            List.of(),
            java.util.Map.of("login", login, "id", id, "name", name),
            "login"
        );
    }
}
