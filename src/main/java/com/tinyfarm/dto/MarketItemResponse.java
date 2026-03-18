package com.tinyfarm.dto;

public record MarketItemResponse(
    String name,
    String seller,
    int price,
    int stock,
    String note
) {
}
