package com.school_system.service.impl;


import com.school_system.classes.AttemptData;
import com.school_system.init.CacheObjects;
import com.school_system.service.LoginAttemptService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;

import static com.school_system.util.TimeUtils.getBlockDuration;


@Service
@Slf4j
@AllArgsConstructor
public class LoginAttempServiceImpl implements LoginAttemptService {

    private static final int MAX_ATTEMPTS = 5;
    private final CacheObjects cacheObjects;


    @Override
    public AttemptData handleFailurLogin(String key) {

        AttemptData attemptUsername = cacheObjects.getKey(key);
        Instant i = Instant.now();
        Duration blockDuration = getBlockDuration(attemptUsername.attempts() + 1);
        AttemptData attemptData = new AttemptData(
                attemptUsername.attempts() + 1, i,
                blockDuration);
        cacheObjects.blockKey(key, attemptData);
        return attemptData;

    }


    @Override
    public int getAttempts(String key) {
        return cacheObjects.getKey(key).attempts();
    }

    @Override
    public void handleSuccessfulResetPassword(String key) {

        log.info("cacheObjects.getKeys(): {}", cacheObjects.getKeys());

        AttemptData attemptData = new AttemptData(
                5, Instant.now(),
                Duration.ofMinutes(5));
        cacheObjects.blockKey("RP-" + key, attemptData);

    }

    @Override
    public boolean isBlocked(String key) {
        AttemptData attemptData = cacheObjects.getKey(key);
        return attemptData != null && attemptData.attempts() >= MAX_ATTEMPTS &&
                Instant.now().isBefore(attemptData.lastAttemptTime().plus(attemptData.blockedDuration()));
    }

    @Override
    public void handleSuccessfulLogin(String key) {
        cacheObjects.getKeys().invalidate(key);
    }

    @Override
    public Instant getUnLockInTime(String key) {

        log.info("getRemainingTimeToUnLockAddress: {}", key);
        AttemptData attemptData = null;

        attemptData = cacheObjects.getKey(key);


        // Check if the number of attempts is at or above the max attempts.
        if (attemptData == null || attemptData.attempts() < MAX_ATTEMPTS) {
            return null; // Not locked out
        }

        return attemptData.lastAttemptTime().plus(attemptData.blockedDuration());


    }

}
