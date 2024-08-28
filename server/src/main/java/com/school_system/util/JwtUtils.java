package com.school_system.util;

import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class JwtUtils {
    public static Set<SimpleGrantedAuthority> buildAuthorities(Set<String> authorities, Set<String> roles) {
        Set<SimpleGrantedAuthority> grantedAuthorities = new HashSet<>();

        // Add authorities from 'authorities' set
        if (authorities != null) {
            grantedAuthorities.addAll(authorities.stream()
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toSet()));
        }

        // Add roles from 'roles' set (prefixing with "" as per Spring Security convention)
        if (roles != null) {
            grantedAuthorities.addAll(roles.stream()
                    .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                    .collect(Collectors.toSet()));
        }

        return grantedAuthorities;
    }
}
