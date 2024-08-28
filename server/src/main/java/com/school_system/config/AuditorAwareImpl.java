package com.school_system.config;

import org.springframework.data.domain.AuditorAware;

import java.util.Optional;

public class AuditorAwareImpl implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {
        // Return the current user's username or ID here.
        // This example returns a static value for simplicity.
        return Optional.of("system");  // Replace with dynamic value
    }
}
/*
    return Optional.ofNullable(SecurityContextHolder.getContext())
        .map(SecurityContext::getAuthentication)
            .filter(Authentication::isAuthenticated)
            .map(Authentication::getPrincipal)
            .map(User.class::cast);*/

/*
@Override
public String getCurrentAuditor() {
    String loggedName = SecurityContextHolder.getContext().getAuthentication().getName();
    return loggedName;
}*/
