package com.school_system.config.listner;

import com.school_system.service.LoginAttemptService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.stereotype.Component;

// TODO listner is not working so use Exceptions Badcredentials to save the LoginFailure and save the IP Address
// Bettwer is to fix this and have a listner to seperate logic of blocing from handle Exceptios
/*
@Slf4j
@Component
public class AuthenticationFailureListener implements
        ApplicationListener<AuthenticationFailureBadCredentialsEvent> {

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private LoginAttemptService loginAttemptService;

    @Override
    public void onApplicationEvent(AuthenticationFailureBadCredentialsEvent e) {
        log.info("onApplicationEvent");
        String ipAddress = getClientIP(request);
        loginAttemptService.loginFailed(ipAddress);

    }
}*/
