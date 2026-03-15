package com.tinyfarm.service;

import com.tinyfarm.domain.GameRules;
import com.tinyfarm.dto.CowActionResponse;
import com.tinyfarm.dto.CowResponse;
import com.tinyfarm.dto.DashboardResponse;
import com.tinyfarm.entity.Cow;
import com.tinyfarm.entity.Farm;
import com.tinyfarm.entity.Inventory;
import com.tinyfarm.repository.CowRepository;
import com.tinyfarm.repository.FarmRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class FarmKernelService {

    private final FarmRepository farmRepository;
    private final CowRepository cowRepository;

    public FarmKernelService(FarmRepository farmRepository, CowRepository cowRepository) {
        this.farmRepository = farmRepository;
        this.cowRepository = cowRepository;
    }

    public DashboardResponse getDashboard(String githubLogin) {
        Farm farm = loadFarm(githubLogin);
        Inventory inventory = farm.getInventory();
        List<CowResponse> cows = farm.getCows().stream()
            .map(cow -> new CowResponse(cow.getId(), cow.getName(), cow.getEnergy(), cow.isReadyToMilk()))
            .toList();

        return new DashboardResponse(
            githubLogin,
            farm.getName(),
            farm.getCoins(),
            inventory.getFeedStock(),
            inventory.getMilkStock(),
            cows
        );
    }

    public CowActionResponse feedCow(String githubLogin, Long cowId) {
        Farm farm = loadFarm(githubLogin);
        Cow cow = loadOwnedCow(farm, cowId);
        Inventory inventory = farm.getInventory();
        if (inventory.getFeedStock() < GameRules.FEED_COST) {
            throw new IllegalStateException("Not enough feed in inventory.");
        }

        inventory.consumeFeed(GameRules.FEED_COST);
        cow.feed(GameRules.FEED_ENERGY_GAIN);
        return new CowActionResponse(
            cow.getId(),
            "feed",
            cow.getEnergy(),
            cow.isReadyToMilk(),
            inventory.getFeedStock(),
            inventory.getMilkStock()
        );
    }

    public CowActionResponse collectMilk(String githubLogin, Long cowId) {
        Farm farm = loadFarm(githubLogin);
        Cow cow = loadOwnedCow(farm, cowId);
        Inventory inventory = farm.getInventory();
        if (!cow.isReadyToMilk()) {
            throw new IllegalStateException("Cow is not ready to milk.");
        }

        inventory.addMilk(GameRules.MILK_GAIN);
        cow.collectMilk();
        return new CowActionResponse(
            cow.getId(),
            "collect-milk",
            cow.getEnergy(),
            cow.isReadyToMilk(),
            inventory.getFeedStock(),
            inventory.getMilkStock()
        );
    }

    private Farm loadFarm(String githubLogin) {
        return farmRepository.findByOwnerGithubLogin(githubLogin)
            .orElseThrow(() -> new IllegalArgumentException("Farm not found for user " + githubLogin));
    }

    private Cow loadOwnedCow(Farm farm, Long cowId) {
        return cowRepository.findById(cowId)
            .filter(cow -> cow.getFarm().getId().equals(farm.getId()))
            .orElseThrow(() -> new IllegalArgumentException("Cow not found for current farm."));
    }
}
