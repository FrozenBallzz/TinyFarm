package com.tinyfarm.dto;

import java.util.List;

public record DashboardResponse(
    String githubLogin,
    String farmName,
    int coins,
    int feedStock,
    int milkStock,
    List<CowResponse> cows
) {
}
