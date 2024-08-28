package com.school_system.util;

import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.util.Map;
@Slf4j
public class TimeUtils {

    private static final Map<Integer, Duration> BLOCK_DURATION_MAP = Map.of(
            5, Duration.ofSeconds(60),
            10, Duration.ofMinutes(5),
            15, Duration.ofMinutes(10),
            20, Duration.ofMinutes(30)
    );

    /**
     * Converts seconds to a time string in the format:
     * - "y hours x minutes z seconds" if all components are present
     * - "x minutes z seconds" if hours are not present
     * - "z seconds" if neither hours nor minutes are present
     *
     * @param seconds the number of seconds to convert
     * @return the time string in a flexible format
     */
    public static String secondsToTimeString(long seconds) {
        // Calculate hours, minutes, and remaining seconds
        long hours = seconds / 3600;
        long minutes = (seconds % 3600) / 60;
        long remainingSeconds = seconds % 60;

        // Build the time string based on the presence of hours, minutes, and seconds
        StringBuilder timeString = new StringBuilder();

        if (hours > 0) {
            timeString.append(hours).append(" Stunden");
            if (hours > 1) {
                timeString.append("s");
            }
        }

        if (minutes > 0) {
            if (!timeString.isEmpty()) {
                timeString.append(" ");
            }
            timeString.append(minutes).append(" Minuten");
            if (minutes > 1) {
                timeString.append("s");
            }
        }

        if (remainingSeconds > 0 || timeString.isEmpty()) { // Ensure seconds are included if no other components are present
            if (!timeString.isEmpty()) {
                timeString.append(" ");
            }
            timeString.append(remainingSeconds).append(" Sekunden");
            if (remainingSeconds > 1) {
                timeString.append("s");
            }
        }

        return timeString.toString();
    }

    public static Duration getBlockDuration(int attempts) {
        log.info("attempts: {}", attempts);
        Duration  d = BLOCK_DURATION_MAP.get(attempts) == null ? Duration.ZERO : BLOCK_DURATION_MAP.get(attempts);
        log.info("d: {}", d.getSeconds());

        return d;
    }
}