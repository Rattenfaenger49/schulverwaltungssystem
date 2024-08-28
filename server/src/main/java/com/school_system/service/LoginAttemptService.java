package com.school_system.service;


import com.school_system.classes.AttemptData;

import java.time.Instant;

public interface LoginAttemptService  {



    boolean isBlocked(String key);

    AttemptData handleFailurLogin(String key);

    Instant getUnLockInTime(String key);
    void handleSuccessfulLogin(String key);
    int getAttempts(String key) ;

    void handleSuccessfulResetPassword(String to);
}
