package com.tinyfarm.dto;

public record CowActionResponse(
    Long cowId,
    String action,
    int energy,
    boolean readyToMilk,
    int feedStock,
    int milkStock
) {
}
