package com.tinyfarm.domain;

import java.time.ZoneId;

public final class GameRules {

    public static final ZoneId FARM_TIME_ZONE = ZoneId.of("Europe/Paris");
    public static final int STARTING_COINS = 1_500;
    public static final int STARTING_STRAW = 4;
    public static final int STARTING_WATER_BUCKETS = 4;
    public static final int STARTING_SOAPS = 2;
    public static final int STARTING_SYRINGES = 2;
    public static final int STARTING_MILK = 0;
    public static final int STARTING_COWS = 1;
    public static final int STARTING_ROOSTERS = 1;
    public static final int STARTING_HENS = 3;
    public static final int STARTING_RABBIT_KITS = 8;
    public static final int LEVEL_ONE_MARKET_PURCHASE_LIMIT = 12;
    public static final int CHICKEN_COOP_CAPACITY = 60;
    public static final int RABBIT_KIT_CAPACITY = 50;
    public static final int RABBIT_ADULT_CAPACITY = 50;
    public static final double STARTING_COW_WEIGHT = 1.0;
    public static final int ADULT_COW_AGE_DAYS = 10;
    public static final double ADULT_COW_WEIGHT = 80.0;
    public static final double MAX_COW_WEIGHT = 750.0;
    public static final double GRASS_WEIGHT_GAIN = 5.0;
    public static final double STRAW_WEIGHT_GAIN = 3.0;
    public static final double WATER_WEIGHT_GAIN = 1.0;
    public static final int FEED_COW_COST = 5;
    public static final int WATER_COW_COST = 2;
    public static final int CLEAN_COW_COST = 3;
    public static final int HEAL_COW_COST = 6;
    public static final int MILK_PRICE_PER_LITER = 2;
    public static final int FULL_MILK_PRODUCTION = 8;
    public static final int REDUCED_MILK_PRODUCTION = 4;
    public static final int MAX_STORED_MILK = 16;

    private GameRules() {
    }
}
