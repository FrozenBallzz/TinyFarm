package com.tinyfarm.dto;

public record CowResponse(
    Long id,
    String name,
    int energy,
    boolean readyToMilk
) {
}
