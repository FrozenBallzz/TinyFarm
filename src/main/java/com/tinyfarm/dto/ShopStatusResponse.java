package com.tinyfarm.dto;

import java.util.List;

public record ShopStatusResponse(
    int dailyPurchaseLimit,
    int purchasesRemaining,
    String cooperativeLabel,
    String cooperativeNote,
    String breedersMarketLabel,
    String breedersMarketNote,
    List<MarketItemResponse> cooperativeItems,
    List<MarketItemResponse> breederItems
) {
}
