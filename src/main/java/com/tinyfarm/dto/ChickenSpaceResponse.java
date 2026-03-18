package com.tinyfarm.dto;

public record ChickenSpaceResponse(
    int roosters,
    int hens,
    int chicks,
    int coopCapacity,
    int eggsReadyToSell,
    String prototypeStatus
) {
}
