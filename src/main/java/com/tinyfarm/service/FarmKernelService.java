package com.tinyfarm.service;

import com.tinyfarm.domain.GameRules;
import com.tinyfarm.dto.CowActionResponse;
import com.tinyfarm.dto.CowResponse;
import com.tinyfarm.dto.DashboardResponse;
import com.tinyfarm.dto.RenameCowRequest;
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
            .map(cow -> new CowResponse(
                cow.getId(),
                cow.getName(),
                cow.getEnergy(),
                cow.getAgeDays(),
                cow.getWeightKg(),
                cow.getMilkAvailableLiters(),
                cow.isAdult(),
                cow.isClean(),
                cow.isSick(),
                cow.isFedToday(),
                cow.isWateredToday(),
                cow.isReadyToMilk()
            ))
            .toList();

        return new DashboardResponse(
            githubLogin,
            farm.getName(),
            farm.getCoins(),
            inventory.getFeedStock(),
            inventory.getWaterStock(),
            inventory.getMilkStock(),
            cows
        );
    }

    public CowActionResponse feedCow(String githubLogin, Long cowId) {
        Farm farm = loadFarm(githubLogin);
        Cow cow = loadOwnedCow(farm, cowId);
        Inventory inventory = farm.getInventory();
        ensureEnoughCoins(farm.getCoins(), GameRules.COW_FEED_ECU_COST, "Not enough coins to feed the cow.");
        if (inventory.getFeedStock() < GameRules.FEED_STOCK_COST) {
            throw new IllegalStateException("Not enough forage in inventory.");
        }
        if (cow.isFedToday()) {
            throw new IllegalStateException("This cow has already eaten today.");
        }

        farm.spendCoins(GameRules.COW_FEED_ECU_COST);
        inventory.consumeFeed(GameRules.FEED_STOCK_COST);
        cow.feed();
        return buildCowActionResponse(farm, inventory, cow, "feed");
    }

    public CowActionResponse waterCow(String githubLogin, Long cowId) {
        Farm farm = loadFarm(githubLogin);
        Cow cow = loadOwnedCow(farm, cowId);
        Inventory inventory = farm.getInventory();
        ensureEnoughCoins(farm.getCoins(), GameRules.COW_WATER_ECU_COST, "Not enough coins to water the cow.");
        if (inventory.getWaterStock() < GameRules.WATER_STOCK_COST) {
            throw new IllegalStateException("Not enough water in inventory.");
        }
        if (cow.isWateredToday()) {
            throw new IllegalStateException("This cow has already been watered today.");
        }

        farm.spendCoins(GameRules.COW_WATER_ECU_COST);
        inventory.consumeWater(GameRules.WATER_STOCK_COST);
        cow.water();
        return buildCowActionResponse(farm, inventory, cow, "water");
    }

    public CowActionResponse cleanCow(String githubLogin, Long cowId) {
        Farm farm = loadFarm(githubLogin);
        Cow cow = loadOwnedCow(farm, cowId);
        Inventory inventory = farm.getInventory();
        ensureEnoughCoins(farm.getCoins(), GameRules.COW_CLEAN_ECU_COST, "Not enough coins to clean the cow.");

        farm.spendCoins(GameRules.COW_CLEAN_ECU_COST);
        cow.clean();
        return buildCowActionResponse(farm, inventory, cow, "clean");
    }

    public CowActionResponse healCow(String githubLogin, Long cowId) {
        Farm farm = loadFarm(githubLogin);
        Cow cow = loadOwnedCow(farm, cowId);
        Inventory inventory = farm.getInventory();
        ensureEnoughCoins(farm.getCoins(), GameRules.COW_HEAL_ECU_COST, "Not enough coins to heal the cow.");

        farm.spendCoins(GameRules.COW_HEAL_ECU_COST);
        cow.heal();
        return buildCowActionResponse(farm, inventory, cow, "heal");
    }

    public CowActionResponse renameCow(String githubLogin, Long cowId, RenameCowRequest renameCowRequest) {
        Farm farm = loadFarm(githubLogin);
        Cow cow = loadOwnedCow(farm, cowId);
        Inventory inventory = farm.getInventory();

        String trimmedName = renameCowRequest.name().trim();
        if (trimmedName.isEmpty()) {
            throw new IllegalArgumentException("Cow name cannot be empty.");
        }

        cow.rename(trimmedName);
        return buildCowActionResponse(farm, inventory, cow, "rename");
    }

    public CowActionResponse advanceCowDay(String githubLogin, Long cowId) {
        Farm farm = loadFarm(githubLogin);
        Cow cow = loadOwnedCow(farm, cowId);
        Inventory inventory = farm.getInventory();

        cow.advanceDay();
        return buildCowActionResponse(farm, inventory, cow, "advance-day");
    }

    public CowActionResponse collectMilk(String githubLogin, Long cowId) {
        Farm farm = loadFarm(githubLogin);
        Cow cow = loadOwnedCow(farm, cowId);
        Inventory inventory = farm.getInventory();
        if (!cow.isAdult()) {
            throw new IllegalStateException("Cow must be adult before producing milk.");
        }
        if (!cow.isReadyToMilk()) {
            throw new IllegalStateException("Cow has no milk ready.");
        }

        inventory.addMilk(cow.collectMilk());
        return buildCowActionResponse(farm, inventory, cow, "collect-milk");
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

    private void ensureEnoughCoins(int currentCoins, int requiredCoins, String message) {
        if (currentCoins < requiredCoins) {
            throw new IllegalStateException(message);
        }
    }

    private CowActionResponse buildCowActionResponse(Farm farm, Inventory inventory, Cow cow, String action) {
        return new CowActionResponse(
            cow.getId(),
            action,
            cow.getName(),
            cow.getEnergy(),
            cow.getAgeDays(),
            cow.getWeightKg(),
            cow.getMilkAvailableLiters(),
            cow.isReadyToMilk(),
            farm.getCoins(),
            inventory.getFeedStock(),
            inventory.getWaterStock(),
            inventory.getMilkStock()
        );
    }
}
