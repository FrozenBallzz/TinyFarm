package com.tinyfarm.service;

import com.tinyfarm.domain.GameRules;
import com.tinyfarm.dto.ChickenSpaceResponse;
import com.tinyfarm.dto.CowActionResponse;
import com.tinyfarm.dto.CowResponse;
import com.tinyfarm.dto.DashboardResponse;
import com.tinyfarm.dto.LeaderboardEntryResponse;
import com.tinyfarm.dto.MarketItemResponse;
import com.tinyfarm.dto.RabbitSpaceResponse;
import com.tinyfarm.dto.ShopStatusResponse;
import com.tinyfarm.dto.TimeStatusResponse;
import com.tinyfarm.entity.Cow;
import com.tinyfarm.entity.Farm;
import com.tinyfarm.entity.Inventory;
import com.tinyfarm.repository.CowRepository;
import com.tinyfarm.repository.FarmRepository;
import com.tinyfarm.util.TimeUtils;
import jakarta.transaction.Transactional;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZonedDateTime;
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
        Instant now = Instant.now();
        LocalDate today = now.atZone(GameRules.FARM_TIME_ZONE).toLocalDate();
        ZonedDateTime farmNow = now.atZone(GameRules.FARM_TIME_ZONE);
        List<CowResponse> cows = farm.getCows().stream()
            .peek(cow -> cow.syncProduction(now))
            .map(cow -> new CowResponse(
                cow.getId(),
                cow.getName(),
                cow.getAgeInDays(now),
                cow.getWeightKg(),
                cow.isAdult(now),
                cow.isClean(),
                cow.isHealthy(),
                cow.hasEatenToday(today),
                cow.hasWaterToday(today),
                cow.getStoredMilkLiters(),
                cow.isReadyToMilk()
            ))
            .toList();

        return new DashboardResponse(
            githubLogin,
            farm.getName(),
            farm.getCoins(),
            inventory.getStrawStock(),
            inventory.getWaterBucketStock(),
            inventory.getSoapStock(),
            inventory.getSyringeStock(),
            inventory.getMilkStock(),
            buildTimeStatus(farmNow),
            buildShopStatus(farmNow),
            new ChickenSpaceResponse(
                GameRules.STARTING_ROOSTERS,
                GameRules.STARTING_HENS,
                0,
                GameRules.CHICKEN_COOP_CAPACITY,
                0,
                "Prototype visuel en place, logique complete a brancher"
            ),
            new RabbitSpaceResponse(
                GameRules.STARTING_RABBIT_KITS,
                0,
                GameRules.RABBIT_KIT_CAPACITY,
                GameRules.RABBIT_ADULT_CAPACITY,
                "Prototype visuel en place, logique complete a brancher"
            ),
            buildLeaderboard(githubLogin),
            cows
        );
    }

    public CowActionResponse feedCow(String githubLogin, Long cowId) {
        return feedCowWithGrass(githubLogin, cowId);
    }

    public CowActionResponse feedCowWithGrass(String githubLogin, Long cowId) {
        Farm farm = loadFarm(githubLogin);
        Cow cow = loadOwnedCow(farm, cowId);
        Instant now = Instant.now();
        cow.syncProduction(now);
        farm.spendCoins(GameRules.FEED_COW_COST);
        cow.feedGrass(now.atZone(GameRules.FARM_TIME_ZONE).toLocalDate());
        return buildActionResponse(farm, cow, "feed-grass");
    }

    public CowActionResponse feedCowWithStraw(String githubLogin, Long cowId) {
        Farm farm = loadFarm(githubLogin);
        Cow cow = loadOwnedCow(farm, cowId);
        Inventory inventory = farm.getInventory();
        if (inventory.getStrawStock() < 1) {
            throw new IllegalStateException("Il n'y a plus de paille dans la remise !");
        }

        Instant now = Instant.now();
        cow.syncProduction(now);
        farm.spendCoins(GameRules.FEED_COW_COST);
        inventory.consumeStraw(1);
        cow.feedStraw(now.atZone(GameRules.FARM_TIME_ZONE).toLocalDate());
        return buildActionResponse(farm, cow, "feed-straw");
    }

    public CowActionResponse waterCow(String githubLogin, Long cowId) {
        Farm farm = loadFarm(githubLogin);
        Cow cow = loadOwnedCow(farm, cowId);
        Inventory inventory = farm.getInventory();
        if (inventory.getWaterBucketStock() < 1) {
            throw new IllegalStateException("Il n'y a plus de seau d'eau dans la remise !");
        }

        Instant now = Instant.now();
        cow.syncProduction(now);
        farm.spendCoins(GameRules.WATER_COW_COST);
        inventory.consumeWaterBucket(1);
        cow.giveWater(now.atZone(GameRules.FARM_TIME_ZONE).toLocalDate());
        return buildActionResponse(farm, cow, "water");
    }

    public CowActionResponse cleanCow(String githubLogin, Long cowId) {
        Farm farm = loadFarm(githubLogin);
        Cow cow = loadOwnedCow(farm, cowId);
        Inventory inventory = farm.getInventory();
        if (inventory.getSoapStock() < 1) {
            throw new IllegalStateException("Il n'y a plus de savon dans la remise !");
        }

        cow.syncProduction(Instant.now());
        farm.spendCoins(GameRules.CLEAN_COW_COST);
        inventory.consumeSoap(1);
        cow.clean();
        return buildActionResponse(farm, cow, "clean");
    }

    public CowActionResponse healCow(String githubLogin, Long cowId) {
        Farm farm = loadFarm(githubLogin);
        Cow cow = loadOwnedCow(farm, cowId);
        Inventory inventory = farm.getInventory();
        if (inventory.getSyringeStock() < 1) {
            throw new IllegalStateException("Il n'y a plus de seringue dans la remise !");
        }

        cow.syncProduction(Instant.now());
        farm.spendCoins(GameRules.HEAL_COW_COST);
        inventory.consumeSyringe(1);
        cow.heal();
        return buildActionResponse(farm, cow, "heal");
    }

    public CowActionResponse collectMilk(String githubLogin, Long cowId) {
        Farm farm = loadFarm(githubLogin);
        Cow cow = loadOwnedCow(farm, cowId);
        Inventory inventory = farm.getInventory();
        cow.syncProduction(Instant.now());
        if (!cow.isReadyToMilk()) {
            throw new IllegalStateException("La vache n'a pas de lait a traire pour le moment !");
        }

        inventory.addMilk(cow.collectMilk());
        return buildActionResponse(farm, cow, "collect-milk");
    }

    private Farm loadFarm(String githubLogin) {
        return farmRepository.findByOwnerGithubLogin(githubLogin)
            .orElseThrow(() -> new IllegalArgumentException("Ferme introuvable pour cet utilisateur."));
    }

    private Cow loadOwnedCow(Farm farm, Long cowId) {
        return cowRepository.findById(cowId)
            .filter(cow -> cow.getFarm().getId().equals(farm.getId()))
            .orElseThrow(() -> new IllegalArgumentException("Cette vache n'existe pas dans la ferme courante."));
    }

    private CowActionResponse buildActionResponse(Farm farm, Cow cow, String action) {
        Inventory inventory = farm.getInventory();
        return new CowActionResponse(
            cow.getId(),
            action,
            cow.getWeightKg(),
            cow.getStoredMilkLiters(),
            cow.isReadyToMilk(),
            farm.getCoins(),
            inventory.getStrawStock(),
            inventory.getWaterBucketStock(),
            inventory.getSoapStock(),
            inventory.getSyringeStock(),
            inventory.getMilkStock()
        );
    }

    private TimeStatusResponse buildTimeStatus(ZonedDateTime farmNow) {
        List<LocalTime[]> cooperativeWindows = isWeekend(farmNow)
            ? List.of(
                new LocalTime[] { LocalTime.of(9, 0), LocalTime.of(14, 0) },
                new LocalTime[] { LocalTime.of(19, 0), LocalTime.of(3, 0) }
            )
            : List.of(
                new LocalTime[] { LocalTime.of(5, 0), LocalTime.of(14, 0) },
                new LocalTime[] { LocalTime.of(17, 0), LocalTime.of(20, 0) },
                new LocalTime[] { LocalTime.of(22, 0), LocalTime.of(3, 0) }
            );

        boolean cooperativeOpen = cooperativeWindows.stream()
            .anyMatch(window -> TimeUtils.isWithinWindow(farmNow.toLocalTime(), window[0], window[1]));

        ZonedDateTime nextWindow = cooperativeOpen ? farmNow : TimeUtils.nextWindowStart(farmNow, cooperativeWindows);
        ZonedDateTime nextMilkWindow = nextMilkWindow(farmNow);

        return new TimeStatusResponse(
            capitalize(TimeUtils.formatDate(farmNow)),
            TimeUtils.formatTime(farmNow),
            dayPhaseLabel(farmNow.toLocalTime()),
            cooperativeOpen,
            cooperativeOpen ? "La cooperative est ouverte maintenant." : "La cooperative est fermee pour le moment.",
            cooperativeOpen
                ? "Fermeture au prochain changement de plage horaire."
                : "Prochaine ouverture : " + TimeUtils.formatDateTime(nextWindow),
            "Prochaine traite naturelle : " + TimeUtils.formatDateTime(nextMilkWindow),
            "Les oeufs pondus doivent etre vendus avant minuit.",
            "Sans nourriture pendant 3 jours, la ferme passe en hibernation automatique.",
            "Apres 50 jours sans retour, le compte est supprime.",
            farmNow.toInstant().toString()
        );
    }

    private ShopStatusResponse buildShopStatus(ZonedDateTime farmNow) {
        int purchasesUsed = (farmNow.getDayOfMonth() + farmNow.getHour()) % 5;
        int purchasesRemaining = Math.max(0, GameRules.LEVEL_ONE_MARKET_PURCHASE_LIMIT - purchasesUsed);
        boolean weekend = isWeekend(farmNow);
        boolean cooperativeOpen = weekend
            ? TimeUtils.isWithinWindow(farmNow.toLocalTime(), LocalTime.of(9, 0), LocalTime.of(14, 0))
                || TimeUtils.isWithinWindow(farmNow.toLocalTime(), LocalTime.of(19, 0), LocalTime.of(3, 0))
            : TimeUtils.isWithinWindow(farmNow.toLocalTime(), LocalTime.of(5, 0), LocalTime.of(14, 0))
                || TimeUtils.isWithinWindow(farmNow.toLocalTime(), LocalTime.of(17, 0), LocalTime.of(20, 0))
                || TimeUtils.isWithinWindow(farmNow.toLocalTime(), LocalTime.of(22, 0), LocalTime.of(3, 0));

        List<MarketItemResponse> cooperativeItems = List.of(
            new MarketItemResponse(
                "Sac de nourriture",
                "Cooperative",
                14,
                10 + (farmNow.getDayOfMonth() % 4),
                "Nourrit une race complete pour la journee."
            ),
            new MarketItemResponse(
                "Seau d'eau",
                "Cooperative",
                6,
                6 + (farmNow.getHour() % 3),
                "Abreuve une race complete."
            ),
            new MarketItemResponse(
                "Savon",
                "Cooperative",
                8,
                3 + (farmNow.getDayOfYear() % 3),
                "Nettoie une race complete."
            ),
            new MarketItemResponse(
                "Seringue",
                "Cooperative",
                11,
                2 + (farmNow.getDayOfWeek().getValue() % 3),
                "Soigne une race complete."
            ),
            new MarketItemResponse(
                "Botte de paille",
                "Cooperative",
                7,
                4 + (farmNow.getHour() % 4),
                "Reservee a la vache des paturages."
            )
        );

        List<MarketItemResponse> breederItems = List.of(
            new MarketItemResponse(
                "Savon ferme",
                "Huguette",
                9,
                1,
                "Article fermier, vendu selon le marche des eleveurs."
            ),
            new MarketItemResponse(
                "Sac de nourriture premium",
                "Roger",
                13,
                2,
                "Prix libre sous plafond, disponible 24h/24."
            ),
            new MarketItemResponse(
                "Seau d'eau",
                "Marinette",
                5,
                1,
                "Le moins cher part en premier."
            )
        );

        return new ShopStatusResponse(
            GameRules.LEVEL_ONE_MARKET_PURCHASE_LIMIT,
            purchasesRemaining,
            cooperativeOpen ? "Cooperative ouverte" : "Cooperative fermee",
            cooperativeOpen
                ? "Stock du jour accessible pendant la plage horaire en cours."
                : "Revenez a la prochaine plage horaire d'ouverture.",
            "Marche des eleveurs ouvert 24h/24",
            "Le plafond de 12 achats est partage entre la cooperative et le marche des eleveurs.",
            cooperativeItems,
            breederItems
        );
    }

    private List<LeaderboardEntryResponse> buildLeaderboard(String githubLogin) {
        return List.of(
            new LeaderboardEntryResponse(1, "Marcel", "Vente de lait", 3_420),
            new LeaderboardEntryResponse(2, "Huguette", "Negociation cooperative", 3_105),
            new LeaderboardEntryResponse(3, capitalize(githubLogin), "Solde", 2_860),
            new LeaderboardEntryResponse(4, "Roger", "Vente de lapins", 2_740),
            new LeaderboardEntryResponse(5, "Marinette", "Vente d'oeufs", 2_615)
        );
    }

    private boolean isWeekend(ZonedDateTime farmNow) {
        return switch (farmNow.getDayOfWeek()) {
            case SATURDAY, SUNDAY -> true;
            default -> false;
        };
    }

    private ZonedDateTime nextMilkWindow(ZonedDateTime farmNow) {
        ZonedDateTime nextSix = TimeUtils.nextOccurrence(farmNow, LocalTime.of(6, 0));
        ZonedDateTime nextEighteen = TimeUtils.nextOccurrence(farmNow, LocalTime.of(18, 0));
        return nextSix.isBefore(nextEighteen) ? nextSix : nextEighteen;
    }

    private String dayPhaseLabel(LocalTime time) {
        if (time.isBefore(LocalTime.of(6, 0))) {
            return "Nuit de ferme";
        }
        if (time.isBefore(LocalTime.NOON)) {
            return "Matin de ferme";
        }
        if (time.isBefore(LocalTime.of(18, 0))) {
            return "Apres-midi de ferme";
        }
        if (time.isBefore(LocalTime.of(22, 0))) {
            return "Soiree de ferme";
        }
        return "Nuit de ferme";
    }

    private String capitalize(String text) {
        if (text == null || text.isBlank()) {
            return text;
        }
        return text.substring(0, 1).toUpperCase() + text.substring(1);
    }
}
