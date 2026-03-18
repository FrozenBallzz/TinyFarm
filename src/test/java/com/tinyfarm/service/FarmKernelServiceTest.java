package com.tinyfarm.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.tinyfarm.dto.CowActionResponse;
import com.tinyfarm.dto.DashboardResponse;
import com.tinyfarm.dto.RenameCowRequest;
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
        assertThat(dashboard.feedStock()).isEqualTo(12);
        assertThat(dashboard.waterStock()).isEqualTo(12);
        assertThat(dashboard.milkStock()).isZero();
        assertThat(dashboard.cows()).hasSize(1);
        assertThat(dashboard.cows().get(0).readyToMilk()).isFalse();
        assertThat(dashboard.cows().get(0).weightKg()).isEqualTo(1);
    }

    @Test
    void feedCowConsumesFeedAndCapsEnergyAtHundred() {
        Long cowId = farmKernelService.getDashboard("tinyfarmer").cows().get(0).id();

        farmKernelService.feedCow("tinyfarmer", cowId);
        farmKernelService.advanceCowDay("tinyfarmer", cowId);
        farmKernelService.feedCow("tinyfarmer", cowId);
        farmKernelService.advanceCowDay("tinyfarmer", cowId);
        CowActionResponse response = farmKernelService.feedCow("tinyfarmer", cowId);

        assertThat(response.action()).isEqualTo("feed");
        assertThat(response.feedStock()).isEqualTo(9);
        assertThat(response.energy()).isLessThanOrEqualTo(100);
    }

    @Test
    void canRenameCow() {
        Long cowId = farmKernelService.getDashboard("tinyfarmer").cows().get(0).id();

        CowActionResponse response = farmKernelService.renameCow("tinyfarmer", cowId, new RenameCowRequest("Marguerite"));

        assertThat(response.name()).isEqualTo("Marguerite");
        assertThat(farmKernelService.getDashboard("tinyfarmer").cows().get(0).name()).isEqualTo("Marguerite");
    }

    @Test
    void collectMilkAddsProducedMilkToInventory() {
        Long cowId = farmKernelService.getDashboard("tinyfarmer").cows().get(0).id();
        for (int i = 0; i < 10; i++) {
            farmKernelService.feedCow("tinyfarmer", cowId);
            farmKernelService.waterCow("tinyfarmer", cowId);
            farmKernelService.advanceCowDay("tinyfarmer", cowId);
        }

        CowActionResponse response = farmKernelService.collectMilk("tinyfarmer", cowId);

        assertThat(response.action()).isEqualTo("collect-milk");
        assertThat(response.milkStock()).isEqualTo(8);
        assertThat(response.readyToMilk()).isFalse();
    }

    @Test
    void collectMilkFailsWhenCowIsNotReady() {
        Long cowId = farmKernelService.getDashboard("tinyfarmer").cows().get(0).id();

        assertThatThrownBy(() -> farmKernelService.collectMilk("tinyfarmer", cowId))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("adult");
    }

    @Test
    void advanceDayMakesCowAdultAndProducesMilkWhenProperlyCaredFor() {
        Long cowId = farmKernelService.getDashboard("tinyfarmer").cows().get(0).id();

        for (int i = 0; i < 10; i++) {
            farmKernelService.feedCow("tinyfarmer", cowId);
            farmKernelService.waterCow("tinyfarmer", cowId);
            farmKernelService.advanceCowDay("tinyfarmer", cowId);
        }

        DashboardResponse dashboard = farmKernelService.getDashboard("tinyfarmer");

        assertThat(dashboard.cows().get(0).adult()).isTrue();
        assertThat(dashboard.cows().get(0).weightKg()).isGreaterThanOrEqualTo(80);
        assertThat(dashboard.cows().get(0).milkAvailableLiters()).isGreaterThanOrEqualTo(8);
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
