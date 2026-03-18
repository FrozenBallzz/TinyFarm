package com.tinyfarm.dto;

import java.util.List;

public record DashboardResponse(
    String githubLogin,
    String farmName,
    int coins,
    int strawStock,
    int waterBucketStock,
    int soapStock,
    int syringeStock,
    int milkStock,
    TimeStatusResponse timeStatus,
    ShopStatusResponse shopStatus,
    ChickenSpaceResponse chickenSpace,
    RabbitSpaceResponse rabbitSpace,
    List<LeaderboardEntryResponse> leaderboard,
    List<CowResponse> cows
) {
}
