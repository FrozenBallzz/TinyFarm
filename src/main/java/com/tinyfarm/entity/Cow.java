package com.tinyfarm.entity;

import com.tinyfarm.domain.GameRules;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

@Entity
@Table(name = "cows")
public class Cow {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "farm_id", nullable = false)
    private Farm farm;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private double weightKg;

    @Column(nullable = false)
    private Instant bornAt;

    @Column(nullable = false)
    private boolean clean;

    @Column(nullable = false)
    private boolean healthy;

    @Column
    private LocalDate lastGrassFedOn;

    @Column
    private LocalDate lastStrawFedOn;

    @Column
    private LocalDate lastWateredOn;

    @Column(nullable = false)
    private int storedMilkLiters;

    @Column(nullable = false)
    private long lastProductionSlot;

    protected Cow() {
    }

    public Cow(Farm farm, String name, double weightKg, Instant bornAt, boolean clean, boolean healthy) {
        this.farm = farm;
        this.name = name;
        this.weightKg = weightKg;
        this.bornAt = bornAt;
        this.clean = clean;
        this.healthy = healthy;
        this.lastProductionSlot = computeProductionSlot(bornAt);
    }

    public Long getId() {
        return id;
    }

    public Farm getFarm() {
        return farm;
    }

    public String getName() {
        return name;
    }

    public double getWeightKg() {
        return weightKg;
    }

    public Instant getBornAt() {
        return bornAt;
    }

    public boolean isClean() {
        return clean;
    }

    public boolean isHealthy() {
        return healthy;
    }

    public int getStoredMilkLiters() {
        return storedMilkLiters;
    }

    public int getAgeInDays(Instant now) {
        return (int) ChronoUnit.DAYS.between(bornAt.atZone(GameRules.FARM_TIME_ZONE).toLocalDate(), now.atZone(GameRules.FARM_TIME_ZONE).toLocalDate());
    }

    public boolean isAdult(Instant now) {
        return getAgeInDays(now) >= GameRules.ADULT_COW_AGE_DAYS && weightKg >= GameRules.ADULT_COW_WEIGHT;
    }

    public boolean hasGrassToday(LocalDate today) {
        return today.equals(lastGrassFedOn);
    }

    public boolean hasStrawToday(LocalDate today) {
        return today.equals(lastStrawFedOn);
    }

    public boolean hasEatenToday(LocalDate today) {
        return hasGrassToday(today) || hasStrawToday(today);
    }

    public boolean hasWaterToday(LocalDate today) {
        return today.equals(lastWateredOn);
    }

    public boolean isReadyToMilk() {
        return storedMilkLiters > 0;
    }

    public void syncProduction(Instant now) {
        long currentSlot = computeProductionSlot(now);
        if (currentSlot <= lastProductionSlot) {
            return;
        }

        for (long slot = lastProductionSlot + 1; slot <= currentSlot; slot++) {
            LocalDate slotDay = slotDate(slot);
            if (!isAdult(slotInstant(slot)) || !clean || !healthy || !hasEatenToday(slotDay) || !hasWaterToday(slotDay)) {
                continue;
            }
            int gain = storedMilkLiters == 0 ? GameRules.FULL_MILK_PRODUCTION : GameRules.REDUCED_MILK_PRODUCTION;
            storedMilkLiters = Math.min(GameRules.MAX_STORED_MILK, storedMilkLiters + gain);
        }

        lastProductionSlot = currentSlot;
    }

    public void feedGrass(LocalDate today) {
        if (hasGrassToday(today)) {
            throw new IllegalStateException("La vache a deja ete nourrie !");
        }
        lastGrassFedOn = today;
        weightKg = Math.min(GameRules.MAX_COW_WEIGHT, weightKg + GameRules.GRASS_WEIGHT_GAIN);
    }

    public void feedStraw(LocalDate today) {
        if (hasStrawToday(today)) {
            throw new IllegalStateException("La vache a deja recu de la paille aujourd'hui !");
        }
        lastStrawFedOn = today;
        weightKg = Math.min(GameRules.MAX_COW_WEIGHT, weightKg + GameRules.STRAW_WEIGHT_GAIN);
    }

    public void giveWater(LocalDate today) {
        if (hasWaterToday(today)) {
            throw new IllegalStateException("La vache a deja ete abreuvee aujourd'hui !");
        }
        lastWateredOn = today;
        if (hasEatenToday(today)) {
            weightKg = Math.min(GameRules.MAX_COW_WEIGHT, weightKg + GameRules.WATER_WEIGHT_GAIN);
        }
    }

    public void clean() {
        clean = true;
    }

    public void heal() {
        healthy = true;
    }

    public int collectMilk() {
        int milk = storedMilkLiters;
        storedMilkLiters = 0;
        return milk;
    }

    private static long computeProductionSlot(Instant instant) {
        ZonedDateTime dateTime = instant.atZone(GameRules.FARM_TIME_ZONE);
        long dayIndex = dateTime.toLocalDate().toEpochDay();
        if (dateTime.getHour() < 6) {
            return dayIndex * 2 - 1;
        }
        return dayIndex * 2 + (dateTime.getHour() >= 18 ? 1 : 0);
    }

    private static Instant slotInstant(long slot) {
        long dayIndex = Math.floorDiv(slot, 2);
        int hour = slot % 2 == 0 ? 6 : 18;
        return LocalDate.ofEpochDay(dayIndex).atTime(hour, 0).atZone(GameRules.FARM_TIME_ZONE).toInstant();
    }

    private static LocalDate slotDate(long slot) {
        return slotInstant(slot).atZone(GameRules.FARM_TIME_ZONE).toLocalDate();
    }
}
