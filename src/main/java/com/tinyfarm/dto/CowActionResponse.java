package com.tinyfarm.dto;

public record CowActionResponse(
    Long cowId,
    String action,
    String name,
    int energy,
    int ageDays,
    int weightKg,
    int milkAvailableLiters,
    boolean readyToMilk,
    int coins,
    int feedStock,
    int waterStock,
    int milkStock
) {
}
