package com.tinyfarm.dto;

public record CowResponse(
    Long id,
    String name,
    int energy,
    int ageDays,
    int weightKg,
    int milkAvailableLiters,
    boolean adult,
    boolean clean,
    boolean sick,
    boolean fedToday,
    boolean wateredToday,
    boolean readyToMilk
) {
}
