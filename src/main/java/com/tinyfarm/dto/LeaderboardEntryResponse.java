package com.tinyfarm.dto;

public record LeaderboardEntryResponse(
    int rank,
    String player,
    String specialty,
    int score
) {
}
