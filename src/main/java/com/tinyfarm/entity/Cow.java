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
    private int energy;

    @Column(nullable = false)
    private int ageDays;

    @Column(nullable = false)
    private int weightKg;

    @Column(nullable = false)
    private int milkAvailableLiters;

    @Column(nullable = false)
    private boolean clean;

    @Column(nullable = false)
    private boolean sick;

    @Column(nullable = false)
    private boolean fedToday;

    @Column(nullable = false)
    private boolean wateredToday;

    @Column(nullable = false)
    private boolean milkedSinceLastProduction;

    protected Cow() {
    }

    public Cow(
        Farm farm,
        String name,
        int energy,
        int ageDays,
        int weightKg,
        int milkAvailableLiters,
        boolean clean,
        boolean sick,
        boolean fedToday,
        boolean wateredToday,
        boolean milkedSinceLastProduction
    ) {
        this.farm = farm;
        this.name = name;
        this.energy = energy;
        this.ageDays = ageDays;
        this.weightKg = weightKg;
        this.milkAvailableLiters = milkAvailableLiters;
        this.clean = clean;
        this.sick = sick;
        this.fedToday = fedToday;
        this.wateredToday = wateredToday;
        this.milkedSinceLastProduction = milkedSinceLastProduction;
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

    public int getEnergy() {
        return energy;
    }

    public int getAgeDays() {
        return ageDays;
    }

    public int getWeightKg() {
        return weightKg;
    }

    public int getMilkAvailableLiters() {
        return milkAvailableLiters;
    }

    public boolean isClean() {
        return clean;
    }

    public boolean isSick() {
        return sick;
    }

    public boolean isFedToday() {
        return fedToday;
    }

    public boolean isWateredToday() {
        return wateredToday;
    }

    public boolean isReadyToMilk() {
        return milkAvailableLiters > 0;
    }

    public boolean isAdult() {
        return ageDays >= GameRules.COW_ADULT_AGE_DAYS && weightKg >= GameRules.COW_ADULT_WEIGHT_KG;
    }

    public void rename(String newName) {
        this.name = newName;
    }

    public void feed() {
        this.fedToday = true;
        this.energy = Math.min(GameRules.COW_MAX_ENERGY, this.energy + GameRules.COW_FEED_ENERGY_GAIN);
    }

    public void water() {
        this.wateredToday = true;
        this.energy = Math.min(GameRules.COW_MAX_ENERGY, this.energy + GameRules.COW_WATER_ENERGY_GAIN);
    }

    public void clean() {
        this.clean = true;
    }

    public void heal() {
        this.sick = false;
    }

    public int collectMilk() {
        int milkCollected = this.milkAvailableLiters;
        this.milkAvailableLiters = 0;
        this.milkedSinceLastProduction = true;
        this.energy = Math.max(0, this.energy - GameRules.COW_MILK_ENERGY_COST);
        return milkCollected;
    }

    public void advanceDay() {
        this.ageDays += 1;

        if (fedToday) {
            this.weightKg = Math.min(
                GameRules.COW_MAX_WEIGHT_KG,
                this.weightKg + GameRules.COW_GRASS_WEIGHT_GAIN + GameRules.COW_STRAW_WEIGHT_GAIN
            );
        } else {
            this.energy = Math.max(0, this.energy - GameRules.COW_DAY_WITHOUT_FOOD_ENERGY_LOSS);
        }

        if (wateredToday) {
            this.weightKg = Math.min(GameRules.COW_MAX_WEIGHT_KG, this.weightKg + GameRules.COW_WATER_WEIGHT_GAIN);
        } else {
            this.energy = Math.max(0, this.energy - GameRules.COW_DAY_WITHOUT_WATER_ENERGY_LOSS);
        }

        if (isAdult() && clean && !sick && fedToday && wateredToday) {
            int producedMilk = milkedSinceLastProduction
                ? GameRules.COW_MILK_IF_MILKED_PREVIOUSLY
                : GameRules.COW_MILK_IF_NOT_MILKED_PREVIOUSLY;
            this.milkAvailableLiters = Math.min(
                GameRules.COW_MAX_MILK_LITERS,
                this.milkAvailableLiters + producedMilk
            );
            this.milkedSinceLastProduction = false;
        }

        this.fedToday = false;
        this.wateredToday = false;
    }
}
