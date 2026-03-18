package com.tinyfarm.dto;

public record CowActionResponse(
    Long cowId,
    String action,
    double weightKg,
    int storedMilkLiters,
    boolean readyToMilk,
    int coins,
    int strawStock,
    int waterBucketStock,
    int soapStock,
    int syringeStock,
    int milkStock
) {
}
