package com.tinyfarm.service;

import com.tinyfarm.domain.GameRules;
import com.tinyfarm.entity.AppUser;
import com.tinyfarm.entity.Cow;
import com.tinyfarm.entity.Farm;
import com.tinyfarm.entity.Inventory;
import com.tinyfarm.repository.AppUserRepository;
import jakarta.transaction.Transactional;
import java.time.Instant;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class GithubUserProvisioningService {

    private final AppUserRepository appUserRepository;

    public GithubUserProvisioningService(AppUserRepository appUserRepository) {
        this.appUserRepository = appUserRepository;
    }

    public void provisionUser(OAuth2User oAuth2User) {
        String githubLogin = oAuth2User.getAttribute("login");
        Number githubId = oAuth2User.getAttribute("id");
        String displayName = oAuth2User.getAttribute("name");
        String resolvedName = displayName == null || displayName.isBlank() ? githubLogin : displayName;

        AppUser existingUser = appUserRepository.findByGithubLogin(githubLogin).orElse(null);
        if (existingUser != null) {
            existingUser.updateDisplayName(resolvedName);
            return;
        }

        AppUser user = new AppUser(githubLogin, githubId.longValue(), resolvedName);
        Farm farm = new Farm(githubLogin + "'s Farm", GameRules.STARTING_COINS, user);
        Inventory inventory = new Inventory(
            farm,
            GameRules.STARTING_STRAW,
            GameRules.STARTING_WATER_BUCKETS,
            GameRules.STARTING_SOAPS,
            GameRules.STARTING_SYRINGES,
            GameRules.STARTING_MILK
        );
        farm.setInventory(inventory);
        for (int i = 0; i < GameRules.STARTING_COWS; i++) {
            farm.addCow(new Cow(farm, "Starter Cow " + (i + 1), GameRules.STARTING_COW_WEIGHT, Instant.now(), true, true));
        }
        user.setFarm(farm);
        appUserRepository.save(user);
    }
}
