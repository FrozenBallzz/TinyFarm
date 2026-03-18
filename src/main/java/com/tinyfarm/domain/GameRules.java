package com.tinyfarm.domain;

public final class GameRules {

    public static final int STARTING_COINS = 1500;
    public static final int STARTING_FEED = 12;
    public static final int STARTING_WATER = 12;
    public static final int STARTING_MILK = 0;
    public static final int STARTING_COWS = 1;

    public static final int COW_STARTING_AGE_DAYS = 0;
    public static final int COW_STARTING_WEIGHT_KG = 1;
    public static final int COW_STARTING_ENERGY = 35;
    public static final int COW_ADULT_AGE_DAYS = 10;
    public static final int COW_ADULT_WEIGHT_KG = 80;
    public static final int COW_MAX_WEIGHT_KG = 750;
    public static final int COW_MAX_ENERGY = 100;
    public static final int COW_MAX_MILK_LITERS = 16;
    public static final int COW_MILK_IF_MILKED_PREVIOUSLY = 8;
    public static final int COW_MILK_IF_NOT_MILKED_PREVIOUSLY = 4;
    public static final int COW_GRASS_WEIGHT_GAIN = 5;
    public static final int COW_STRAW_WEIGHT_GAIN = 3;
    public static final int COW_WATER_WEIGHT_GAIN = 1;
    public static final int COW_FEED_ENERGY_GAIN = 20;
    public static final int COW_WATER_ENERGY_GAIN = 10;
    public static final int COW_MILK_ENERGY_COST = 10;
    public static final int COW_DAY_WITHOUT_FOOD_ENERGY_LOSS = 25;
    public static final int COW_DAY_WITHOUT_WATER_ENERGY_LOSS = 15;

    public static final int FEED_STOCK_COST = 1;
    public static final int WATER_STOCK_COST = 1;
    public static final int COW_FEED_ECU_COST = 5;
    public static final int COW_WATER_ECU_COST = 2;
    public static final int COW_CLEAN_ECU_COST = 3;
    public static final int COW_HEAL_ECU_COST = 6;

    private GameRules() {
    }
}
