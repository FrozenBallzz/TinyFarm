package com.tinyfarm.dto;

public record TimeStatusResponse(
    String currentDateLabel,
    String currentTimeLabel,
    String dayPhaseLabel,
    boolean cooperativeOpen,
    String cooperativeStatusLabel,
    String nextCooperativeWindowLabel,
    String nextMilkWindowLabel,
    String eggDeadlineLabel,
    String hibernationWarningLabel,
    String deletionWarningLabel,
    String currentTimeIso
) {
}
