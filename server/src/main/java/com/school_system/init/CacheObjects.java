package com.school_system.init;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.school_system.classes.AttemptData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ExecutionException;
@Slf4j
@Component
public class CacheObjects {


    private static final int MAXIMUM_SIZE = 1000;
    private static final Duration EXPIRATION_TIME = Duration.ofMinutes(30);


    private static final LoadingCache<String, AttemptData> BLOCKED_KEYS = CacheBuilder.newBuilder()
            .maximumSize(MAXIMUM_SIZE)
            .expireAfterWrite(EXPIRATION_TIME)
            .build(new CacheLoader<>() {
                @Override
                public AttemptData load(String key) {
                    return new AttemptData(0, Instant.now(), Duration.ZERO);
                }
            });

    private CacheObjects() {
    }


    public  LoadingCache<String, AttemptData> getKeys() {
        return BLOCKED_KEYS;
    }
    public  AttemptData getKey(String key) {
        try {
            return BLOCKED_KEYS.get(key);

        }catch (ExecutionException e) {
            log.error("Error retrieving attempt data", e);
            return new AttemptData(0, Instant.now(), Duration.ZERO);

        }
    }
    public  void blockKey(String key, AttemptData data) {
        log.info("Attempt blocked key {}, data: {}", key,data);
        BLOCKED_KEYS.put(key, data);

    }
}
