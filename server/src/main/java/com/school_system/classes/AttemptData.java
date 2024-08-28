package com.school_system.classes;

import java.time.Duration;
import java.time.Instant;

public record AttemptData(int attempts, Instant lastAttemptTime, Duration blockedDuration) {

}