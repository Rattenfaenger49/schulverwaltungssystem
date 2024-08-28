package com.school_system.auth;

import com.school_system.dto.CustomUserDetails;
import com.school_system.entity.security.User;
import com.school_system.exception.RateLimitingException;
import com.school_system.exception.UserNotVerifiedException;
import com.school_system.mapper.Mapper;
import com.school_system.repository.UserRepository;
import com.school_system.service.LoginAttemptService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AccountStatusException;
import org.springframework.security.authentication.AccountStatusUserDetailsChecker;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import org.springframework.stereotype.Service;



@Slf4j
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final LoginAttemptService loginAttemptService;
    private final HttpServletRequest request;

    public CustomUserDetailsService(UserRepository userRepository, LoginAttemptService loginAttemptService, HttpServletRequest request) {
        this.userRepository = userRepository;
        this.loginAttemptService = loginAttemptService;
        this.request = request;
    }

    @Override
    public CustomUserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        // Filter Blocked user
        if (loginAttemptService.isBlocked(username)) {
            throw new RateLimitingException("Gesperrt aufgrund zu vieler fehlgeschlagener Anmeldeversuche("+loginAttemptService.getAttempts(username)+"). Abgesperrt at: "+
                    loginAttemptService.getUnLockInTime(username));
        }
        User user = userRepository.findByUsername(username).orElseThrow(() ->
                new UsernameNotFoundException(String.format("Benutzername %s wurde nicht gefunden.", username)));

        if (!user.isVerified()) {
            throw new UserNotVerifiedException("Benutzer ist nicht verifiziert.");
        }
        CustomUserDetails customUserDetails = Mapper.toCustomUserDetails(user);

        try {
            new AccountStatusUserDetailsChecker().check(customUserDetails);
        } catch (AccountStatusException e) {
            log.error("Benutzer konnte nicht authentifiziert werden.", e);
            throw new RuntimeException(e.getMessage());
        }


        return customUserDetails;
    }
}
