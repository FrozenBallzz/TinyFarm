package com.tinyfarm.dto;

public record RabbitSpaceResponse(
    int rabbitKits,
    int adultRabbits,
    int kitCapacity,
    int adultCapacity,
    String prototypeStatus
) {
}
