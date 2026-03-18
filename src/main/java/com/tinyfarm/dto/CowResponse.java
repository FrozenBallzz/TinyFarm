package com.tinyfarm.dto;

public record CowResponse(
    Long id,
    String name,
    int ageDays,
    double weightKg,
    boolean adult,
    boolean clean,
    boolean healthy,
    boolean ateToday,
    boolean drankToday,
    int storedMilkLiters,
    boolean readyToMilk
) {
}
