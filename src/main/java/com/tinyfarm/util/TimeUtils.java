package com.tinyfarm.util;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

public final class TimeUtils {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("EEEE d MMMM yyyy", Locale.FRENCH);
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm", Locale.FRENCH);

    private TimeUtils() {
    }

    public static Instant now() {
        return Instant.now();
    }

    public static String formatDate(ZonedDateTime dateTime) {
        return DATE_FORMATTER.format(dateTime);
    }

    public static String formatTime(ZonedDateTime dateTime) {
        return TIME_FORMATTER.format(dateTime);
    }

    public static String formatDateTime(ZonedDateTime dateTime) {
        return DATE_FORMATTER.format(dateTime) + " a " + TIME_FORMATTER.format(dateTime);
    }

    public static ZonedDateTime nextOccurrence(ZonedDateTime now, LocalTime time) {
        ZonedDateTime candidate = now.with(time).withSecond(0).withNano(0);
        if (!candidate.isAfter(now)) {
            candidate = candidate.plusDays(1);
        }
        return candidate;
    }

    public static boolean isWithinWindow(LocalTime time, LocalTime startInclusive, LocalTime endExclusive) {
        if (startInclusive.equals(endExclusive)) {
            return true;
        }
        if (startInclusive.isBefore(endExclusive)) {
            return !time.isBefore(startInclusive) && time.isBefore(endExclusive);
        }
        return !time.isBefore(startInclusive) || time.isBefore(endExclusive);
    }

    public static ZonedDateTime nextWindowStart(ZonedDateTime now, List<LocalTime[]> windows) {
        for (int dayOffset = 0; dayOffset < 8; dayOffset++) {
            LocalDate date = now.toLocalDate().plusDays(dayOffset);
            for (LocalTime[] window : windows) {
                LocalDateTime localDateTime = date.atTime(window[0]);
                ZonedDateTime candidate = localDateTime.atZone(now.getZone());
                if (dayOffset == 0 && !candidate.isAfter(now)) {
                    continue;
                }
                return candidate;
            }
        }
        return now;
    }
}
