package com.tinyfarm.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.tinyfarm.dto.CowActionResponse;
import com.tinyfarm.dto.DashboardResponse;
import com.tinyfarm.entity.Cow;
import com.tinyfarm.repository.AppUserRepository;
import com.tinyfarm.repository.CowRepository;
import com.tinyfarm.repository.FarmRepository;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.util.ReflectionTestUtils;

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

    @Autowired
    private CowRepository cowRepository;

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
        assertThat(dashboard.coins()).isEqualTo(1_500);
        assertThat(dashboard.strawStock()).isEqualTo(4);
        assertThat(dashboard.waterBucketStock()).isEqualTo(4);
        assertThat(dashboard.chickenSpace().hens()).isEqualTo(3);
        assertThat(dashboard.chickenSpace().roosters()).isEqualTo(1);
        assertThat(dashboard.rabbitSpace().rabbitKits()).isEqualTo(8);
        assertThat(dashboard.timeStatus().currentTimeIso()).isNotBlank();
        assertThat(dashboard.timeStatus().nextMilkWindowLabel()).contains("Prochaine traite");
        assertThat(dashboard.timeStatus().eggDeadlineLabel()).contains("minuit");
        assertThat(dashboard.shopStatus().dailyPurchaseLimit()).isEqualTo(12);
        assertThat(dashboard.shopStatus().cooperativeItems()).hasSize(5);
        assertThat(dashboard.shopStatus().breederItems()).hasSize(3);
        assertThat(dashboard.leaderboard()).hasSize(5);
        assertThat(dashboard.milkStock()).isZero();
        assertThat(dashboard.cows()).hasSize(1);
        assertThat(dashboard.cows().get(0).adult()).isFalse();
        assertThat(dashboard.cows().get(0).readyToMilk()).isFalse();
    }

    @Test
    void feedCowWithGrassConsumesCoinsAndIncreasesWeight() {
        Long cowId = farmKernelService.getDashboard("tinyfarmer").cows().get(0).id();

        CowActionResponse response = farmKernelService.feedCow("tinyfarmer", cowId);

        assertThat(response.action()).isEqualTo("feed-grass");
        assertThat(response.coins()).isEqualTo(1_495);
        assertThat(response.weightKg()).isEqualTo(6.0);
        assertThat(response.strawStock()).isEqualTo(4);
        assertThat(response.readyToMilk()).isFalse();
    }

    @Test
    void waterAfterFeedingUsesBucketAndAddsWeight() {
        Long cowId = farmKernelService.getDashboard("tinyfarmer").cows().get(0).id();
        farmKernelService.feedCow("tinyfarmer", cowId);

        CowActionResponse response = farmKernelService.waterCow("tinyfarmer", cowId);

        assertThat(response.action()).isEqualTo("water");
        assertThat(response.coins()).isEqualTo(1_493);
        assertThat(response.waterBucketStock()).isEqualTo(3);
        assertThat(response.weightKg()).isEqualTo(7.0);
    }

    @Test
    void collectMilkAddsProducedMilkToInventory() {
        Long cowId = farmKernelService.getDashboard("tinyfarmer").cows().get(0).id();
        matureAndPrepareCowForMilk();

        CowActionResponse response = farmKernelService.collectMilk("tinyfarmer", cowId);

        assertThat(response.action()).isEqualTo("collect-milk");
        assertThat(response.milkStock()).isEqualTo(8);
        assertThat(response.readyToMilk()).isFalse();
        assertThat(response.storedMilkLiters()).isZero();
    }

    @Test
    void collectMilkFailsWhenCowIsNotReady() {
        Long cowId = farmKernelService.getDashboard("tinyfarmer").cows().get(0).id();

        assertThatThrownBy(() -> farmKernelService.collectMilk("tinyfarmer", cowId))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("n'a pas de lait");
    }

    @Test
    void feedingCowTwiceInSameDayShowsFrenchRuleMessage() {
        Long cowId = farmKernelService.getDashboard("tinyfarmer").cows().get(0).id();
        farmKernelService.feedCow("tinyfarmer", cowId);

        assertThatThrownBy(() -> farmKernelService.feedCow("tinyfarmer", cowId))
            .isInstanceOf(IllegalStateException.class)
            .hasMessage("La vache a deja ete nourrie !");
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

    private void matureAndPrepareCowForMilk() {
        Long cowId = farmKernelService.getDashboard("tinyfarmer").cows().get(0).id();
        Cow cow = cowRepository.findById(cowId).orElseThrow();
        Instant now = Instant.now();
        LocalDate today = now.atZone(com.tinyfarm.domain.GameRules.FARM_TIME_ZONE).toLocalDate();
        ReflectionTestUtils.setField(cow, "bornAt", now.minus(12, ChronoUnit.DAYS));
        ReflectionTestUtils.setField(cow, "weightKg", 82.0d);
        ReflectionTestUtils.setField(cow, "clean", true);
        ReflectionTestUtils.setField(cow, "healthy", true);
        ReflectionTestUtils.setField(cow, "lastGrassFedOn", today);
        ReflectionTestUtils.setField(cow, "lastWateredOn", today);
        ReflectionTestUtils.setField(cow, "lastProductionSlot", computeProductionSlot(now) - 1);
        ReflectionTestUtils.setField(cow, "storedMilkLiters", 0);
        cowRepository.save(cow);
    }

    private long computeProductionSlot(Instant instant) {
        ZonedDateTime dateTime = instant.atZone(com.tinyfarm.domain.GameRules.FARM_TIME_ZONE);
        long dayIndex = dateTime.toLocalDate().toEpochDay();
        if (dateTime.getHour() < 6) {
            return dayIndex * 2 - 1;
        }
        return dayIndex * 2 + (dateTime.getHour() >= 18 ? 1 : 0);
    }
}
